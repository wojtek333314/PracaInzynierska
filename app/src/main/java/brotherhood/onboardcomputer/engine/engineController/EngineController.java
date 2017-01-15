package brotherhood.onboardcomputer.engine.engineController;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class EngineController {
    public static boolean DEMO = false;
    public static int UPDATE_INTERVAL = 500;
    public static final int MIN_UPDATE_INTERVAL = 500;
    private static final String DEVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothSocket socket = null;

    private Random random = new Random();
    private CommandListener commandListener;

    private boolean pidsSupportedChecked;
    private CommandsAvailabilityController commandsAvailabilityController;
    private HashMap<CommandListener, EngineCommand> additionalQueue;
    private boolean controllerThreadRunning = true;
    private boolean collectingData;

    public EngineController(String deviceAddress, CommandListener commandListener) throws IOException, InterruptedException {
        this.commandListener = commandListener;
        this.commandsAvailabilityController = new CommandsAvailabilityController();
        this.additionalQueue = new HashMap<>();
        if (DEMO) {
            initTimer();
            return;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        UUID uuid = UUID.fromString(DEVICE_UUID);
        BluetoothSocket socket;
        socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        if (socket != null) {
            try {
                socket.connect();
            }catch (IOException e){
                Class<?> clazz = socket.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

                Method m = null;
                try {
                    m = clazz.getMethod("createRfcommSocket", paramTypes);

                Object[] params = new Object[] {Integer.valueOf(1)};
                BluetoothSocket tmpSocket = socket;
                socket = (BluetoothSocket) m.invoke(tmpSocket.getRemoteDevice(), params);
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                socket.connect();

            }
            initializeOBDconnection();
        }
        this.socket = socket;
        initTimer();
    }

    private void initTimer() {
        checkSupportedCommands(socket);
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (controllerThreadRunning) {
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

    private void initializeOBDconnection() throws IOException, InterruptedException {
        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new TimeoutCommand(MIN_UPDATE_INTERVAL / 2).run(socket.getInputStream(), socket.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
    }

    private void checkSupportedCommands(final BluetoothSocket socket) {
        if (!DEMO) {
            try {
                commandsAvailabilityController.getPidsSupported01_20().run(socket.getInputStream(), socket.getOutputStream());
                commandsAvailabilityController.getPidsSupported21_40().run(socket.getInputStream(), socket.getOutputStream());
                commandsAvailabilityController.getPidsSupported41_60().run(socket.getInputStream(), socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            commandsAvailabilityController.prepareDemoAvailability();
        }
        commandsAvailabilityController.updatePidsAvailability();
        pidsSupportedChecked = true;
        System.out.println("check supported voiceAssistanceCommand finished");
    }


    private void updatePids(BluetoothSocket socket) {
        long time = System.currentTimeMillis();
        if (collectingData) {
            return;
        }
        collectingData = true;
        try {
            if (DEMO) {
                for (EngineCommand engineCommand : commandsAvailabilityController.getEngineCommands()) {
                    int demoValue = random.nextInt(300);
                    engineCommand.addValue(String.valueOf(demoValue));
                }
            } else {
                for (EngineCommand engineCommand : commandsAvailabilityController.getOnlyAvailableEngineCommands()) {
                    try {
                        engineCommand.run(socket.getInputStream(), socket.getOutputStream());
                    } catch (NoDataException e) {
                        System.out.println(engineCommand.getName() + " NO DATA! ");
                        commandListener.onNoData(engineCommand);
                    }
                }
                updateAdditionalPids();
                additionalQueue.clear();
            }
            if (commandListener != null) {
                commandListener.onDataRefresh();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        collectingData = false;
        System.out.println("end update" + (time - System.currentTimeMillis()));
    }

    private void updateAdditionalPids() throws IOException, InterruptedException {
        for (CommandListener commandListenerKey : additionalQueue.keySet()) {
            try {
                additionalQueue.get(commandListenerKey).run(socket.getInputStream(), socket.getOutputStream());
                commandListenerKey.onDataRefresh();
            } catch (NoDataException e) {
                System.out.println(additionalQueue.get(commandListenerKey).getName() + " NO DATA! ");
                commandListenerKey.onNoData(additionalQueue.get(commandListenerKey));
            }
        }
        additionalQueue.clear();
    }

    public void addCommandToQueue(EngineCommand engineCommand, CommandListener commandListener) {
        additionalQueue.put(commandListener, engineCommand);
    }

    public CommandsAvailabilityController getCommandsAvailabilityController() {
        return commandsAvailabilityController;
    }

    public void destroy() {
        controllerThreadRunning = false;
    }

    public boolean canCollectData() {
        return pidsSupportedChecked;
    }

    public interface CommandListener {
        void onDataRefresh();

        void onNoData(EngineCommand engineCommand);
    }
}
