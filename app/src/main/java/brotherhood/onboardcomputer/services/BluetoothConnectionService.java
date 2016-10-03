package brotherhood.onboardcomputer.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import brotherhood.onboardcomputer.ecuCommands.Command;
import brotherhood.onboardcomputer.ecuCommands.Pid;
import brotherhood.onboardcomputer.ecuCommands.PidsSupportedCommand;

public class BluetoothConnectionService extends Service {
    public static int UPDATE_INTERVAL = 10;
    public static final String INTENT_FILTER_TAG = "engineData";
    public static final String DEVICE_ADDRESS_KEY = "deviceAddress";
    public static final String REFRESH_FRAME = "refreshFrame";
    private static final String DEVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private String deviceAddress = null;
    private ArrayList<Pid> pidsSupported = new ArrayList<>();
    private ArrayList<Command> commands = new ArrayList<>();
    private boolean serviceRunning = true;
    private boolean pidsSupportedChecked;

    @Override
    public IBinder onBind(Intent intent) {
        deviceAddress = intent.getExtras().getString(DEVICE_ADDRESS_KEY, null);
        return null;
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_FILTER_TAG);
    }

    private void initTimer(final BluetoothSocket socket) {
        checkSupportedCommands(socket);
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (serviceRunning) {
                    try {
                        Thread.sleep(UPDATE_INTERVAL);
                        if (pidsSupportedChecked) {
                            collectData(socket);
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        deviceAddress = intent.getExtras().getString(DEVICE_ADDRESS_KEY, null);
        System.out.println("DEVICE ADRESS SERVICE:" + deviceAddress);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
        UUID uuid = UUID.fromString(DEVICE_UUID);
        BluetoothSocket socket = null;
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (socket != null)
                socket.connect();
            initializeOBDconnection(socket);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        initTimer(socket);
        return START_STICKY;
    }

    private void initializeOBDconnection(BluetoothSocket socket) throws IOException, InterruptedException {
        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
    }

    private void checkSupportedCommands(final BluetoothSocket socket) {
        PidsSupportedCommand supportedPids = new PidsSupportedCommand(this, PidsSupportedCommand.Range.PIDS_01_20);
        PidsSupportedCommand supportedPids2 = new PidsSupportedCommand(this, PidsSupportedCommand.Range.PIDS_21_40);

        try {
            supportedPids.run(socket.getInputStream(), socket.getOutputStream());
            supportedPids2.run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        pidsSupported = supportedPids.getResponse();
        for (Pid pid : supportedPids.getResponse()) {
            System.out.println(pid.getCommand() + "/" + pid.isSupported());
            commands.add(new Command(pid));
        }

        pidsSupportedChecked = true;
    }

    private void collectData(final BluetoothSocket socket) throws IOException, InterruptedException {
        updatePids(socket);
        Intent intent = new Intent(INTENT_FILTER_TAG);
        intent.putExtra(REFRESH_FRAME, pidsSupported);
        sendBroadcast(intent);
    }

    private void updatePids(BluetoothSocket socket) {

        try {
          /*  commands.get(5).run(socket.getInputStream(),socket.getOutputStream());
            commands.get(12).run(socket.getInputStream(),socket.getOutputStream());
            commands.get(13).run(socket.getInputStream(),socket.getOutputStream());*/
            for(Command command : commands){
                command.run(socket.getInputStream(), socket.getOutputStream());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        serviceRunning = false;
        return super.onUnbind(intent);
    }
}
