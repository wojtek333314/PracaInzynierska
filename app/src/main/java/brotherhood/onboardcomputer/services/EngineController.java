package brotherhood.onboardcomputer.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

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

public class EngineController {
    public static boolean DEMO = false;
    public static int UPDATE_INTERVAL = 1000;
    private static final String DEVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private static final int MIN_UPDATE_INTERVAL = 90;

    private Context context;
    private Random random = new Random();
    private ArrayList<Pid> pidsSupported = new ArrayList<>();
    private ArrayList<Command> commandsSupported = new ArrayList<>();
    private EngineListener engineListener;

    private boolean collectingData = true;
    private boolean pidsSupportedChecked;

    public EngineController(Context context, String deviceAddress, EngineListener engineListener) throws IOException, InterruptedException {
        this.context = context;
        this.engineListener = engineListener;

        if (DEMO) {
            initTimer(null);
            return;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        UUID uuid = UUID.fromString(DEVICE_UUID);
        BluetoothSocket socket = null;
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
        PidsSupportedCommand supportedPids = new PidsSupportedCommand(context, PidsSupportedCommand.Range.PIDS_01_20);
        PidsSupportedCommand supportedPids2 = new PidsSupportedCommand(context, PidsSupportedCommand.Range.PIDS_21_40);

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
        for (Pid pid : pidsSupported) {
            if (pid.isSupported()) {
                System.out.println(pid.getCommand() + "/" + pid.isSupported());
                commandsSupported.add(new Command(pid));
            }
        }

        pidsSupportedChecked = true;
        System.out.println("check supported command, count of supported pids: " + commandsSupported.size());
    }

    private void createDemoPids() {
        System.out.println("demo pids created!");
        try {
            JSONArray jsonArray = new JSONArray(Helper.loadJSONFromAsset(context, "pids.json"));
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
            for (Command command : commandsSupported) {
                if (!collectingData) {
                    return;
                }
                if (!DEMO) {
                    command.run(socket.getInputStream(), socket.getOutputStream());
                } else {
                    int demoValue = random.nextInt(300);
                    command.getPid().addValue(String.valueOf(demoValue));
                }
            }
            if (engineListener != null) {
                engineListener.onDataRefresh(pidsSupported);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Pid> getPidsSupported() {
        return pidsSupported;
    }

    public void destroy() {
        collectingData = false;
    }

    public boolean canCollectData() {
        return pidsSupportedChecked;
    }

    public interface EngineListener {
        void onDataRefresh(ArrayList<Pid> pidsSupported);
    }
}
