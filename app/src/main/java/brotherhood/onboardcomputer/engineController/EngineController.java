package brotherhood.onboardcomputer.engineController;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import brotherhood.onboardcomputer.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.ecuCommands.mode1.Mode1Controller;

public class EngineController {
    public static boolean DEMO = false;
    public static int UPDATE_INTERVAL = 1000;
    private static final String DEVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private static final int MIN_UPDATE_INTERVAL = 90;

    private Context context;
    private Random random = new Random();
    private EngineListener engineListener;

    private boolean collectingData = true;
    private boolean pidsSupportedChecked;
    private Mode1Controller mode1Controller;

    public EngineController(Context context, String deviceAddress, EngineListener engineListener) throws IOException, InterruptedException {
        this.context = context;
        this.engineListener = engineListener;
        this.mode1Controller = new Mode1Controller();
        if (DEMO) {
            initTimer(null);
            return;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        UUID uuid = UUID.fromString(DEVICE_UUID);
        BluetoothSocket socket;
        socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        if (socket != null) {
            socket.connect();
            initializeOBDconnection(socket);
        }
        initTimer(socket);
    }

    private void initTimer(final BluetoothSocket socket) {
        checkSupportedCommands(socket);
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (collectingData) {
                    try {
                        if (UPDATE_INTERVAL < MIN_UPDATE_INTERVAL) {
                            UPDATE_INTERVAL = MIN_UPDATE_INTERVAL;
                        }
                        Thread.sleep(UPDATE_INTERVAL);
                        if (canCollectData()) {
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

    private void initializeOBDconnection(BluetoothSocket socket) throws IOException, InterruptedException {
        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new TimeoutCommand(MIN_UPDATE_INTERVAL).run(socket.getInputStream(), socket.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
    }

    private void checkSupportedCommands(final BluetoothSocket socket) {
        if (!DEMO) {
            try {
                mode1Controller.getPidsSupported01_20().run(socket.getInputStream(), socket.getOutputStream());
                mode1Controller.getPidsSupported21_40().run(socket.getInputStream(), socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mode1Controller.prepareDemoAvailability();
        }
        mode1Controller.updatePidsAvailability();
        pidsSupportedChecked = true;
        System.out.println("check supported command finished");
    }

    private void updatePids(BluetoothSocket socket) {
        try {
            if (DEMO) {
                if (!collectingData) {
                    return;
                }
                for (EngineCommand engineCommand : mode1Controller.getEngineCommands()) {
                    int demoValue = random.nextInt(300);
                    engineCommand.addValue(String.valueOf(demoValue));
                }
            } else {
                if (!collectingData) {
                    return;
                }
                System.out.println("updateing pids count:" + mode1Controller.getOnlyAvailableEngineCommands().length);
                for (EngineCommand engineCommand : mode1Controller.getOnlyAvailableEngineCommands()) {
                    try {
                        engineCommand.run(socket.getInputStream(), socket.getOutputStream());
                    } catch (NoDataException e) {
                        System.out.println(engineCommand.getName() + " NO DATA! ");
                    }
                }
            }
            if (engineListener != null) {
                engineListener.onDataRefresh();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Mode1Controller getMode1Controller() {
        return mode1Controller;
    }

    public void destroy() {
        collectingData = false;
    }

    public boolean canCollectData() {
        return pidsSupportedChecked;
    }

    public interface EngineListener {
        void onDataRefresh();
    }
}
