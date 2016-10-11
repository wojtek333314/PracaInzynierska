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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import brotherhood.onboardcomputer.ecuCommands.Command;
import brotherhood.onboardcomputer.ecuCommands.Pid;
import brotherhood.onboardcomputer.ecuCommands.PidsSupportedCommand;
import brotherhood.onboardcomputer.utils.Helper;

public class BluetoothConnectionService extends Service {
    public static boolean DEMO = false;
    public static int UPDATE_INTERVAL = 1000;
    public static final String INTENT_FILTER_TAG = "engineData";
    public static final String DEVICE_ADDRESS_KEY = "deviceAddress";
    public static final String REFRESH_FRAME = "refreshFrame";
    private static final String DEVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private static final int MIN_UPDATE_INTERVAL = 20;

    private String deviceAddress = null;
    public static ArrayList<Pid> pidsSupported = new ArrayList<>();
    private ArrayList<Command> commands = new ArrayList<>();
    public static boolean serviceRunning = true;
    private boolean pidsSupportedChecked;
    private int demoValue = 1;
    private Random random = new Random();

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
                        if (UPDATE_INTERVAL < MIN_UPDATE_INTERVAL) {
                            UPDATE_INTERVAL = MIN_UPDATE_INTERVAL;
                        }
                        Thread.sleep(UPDATE_INTERVAL);
                        if (pidsSupportedChecked) {
                            updatePids(socket);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        if (DEMO) {
            initTimer(null);
            return START_STICKY;
        }
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

        if (!DEMO) {
            try {
                supportedPids.run(socket.getInputStream(), socket.getOutputStream());
                supportedPids2.run(socket.getInputStream(), socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pidsSupported = supportedPids.getResponse();
        if (pidsSupported.size() == 0 && DEMO) {
            createDemoPids();
        }
        for (Pid pid : supportedPids.getResponse()) {
            if (pid.isSupported()) {
                System.out.println(pid.getCommand() + "/" + pid.isSupported());
                commands.add(new Command(pid));
            }
        }

        pidsSupportedChecked = true;
        System.out.println("check supported command, count of supported pids: " + commands.size());
    }

    private void createDemoPids() {
        System.out.println("demo pids created!");
        try {
            JSONArray jsonArray = new JSONArray(Helper.loadJSONFromAsset(getApplication(), "pids.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                pidsSupported.add(new Pid(object.getString("command")
                        , object.getString("description")
                        , object.getString("calculationsScript")
                        , object.getString("unit")
                        , true));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updatePids(BluetoothSocket socket) {
        try {
            for (Command command : commands) {
                if (!DEMO) {
                    command.run(socket.getInputStream(), socket.getOutputStream());
                } else {
                    demoValue = random.nextInt(300);
                    command.getPid().addValue(String.valueOf(demoValue));
                }
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
