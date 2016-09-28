package brotherhood.onboardcomputer.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.UUID;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.ecuCommands.CoolantTemperatureCommand;
import brotherhood.onboardcomputer.ecuCommands.EngineLoadCommand;
import brotherhood.onboardcomputer.ecuCommands.FuelRailAbsolutePressureCommand;
import brotherhood.onboardcomputer.ecuCommands.FuelRateCommand;
import brotherhood.onboardcomputer.ecuCommands.Pid;
import brotherhood.onboardcomputer.ecuCommands.PidsSupportedCommand;
import brotherhood.onboardcomputer.utils.Helper;

public class BluetoothConnectionService extends Service {
    public static final boolean DEMO = false;
    public static int UPDATE_INTERVAL = 1000;
    public static final String DEVICE_ADDRESS_KEY = "deviceAddress";
    public static final String REFRESH_FRAME = "refreshFrame";
    private static final String DEVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private LinkedHashMap<String, Pid> availablePidsMap = new LinkedHashMap<>();

    private String deviceAddress = null;
    private boolean serviceRunning = true;
    private EngineData engineData;
    private boolean pidsChecked;

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        fillAvailablePidsMap();
        final BluetoothSocket socket = connect(intent);

        Thread refreshDataTimer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (serviceRunning) {
                    try {
                        Thread.sleep(UPDATE_INTERVAL);
                        timeRefresh(socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        refreshDataTimer.start();
        return START_STICKY;
    }

    private void timeRefresh(BluetoothSocket socket) throws IOException, InterruptedException {
        System.out.println("Data Refresh:" + UPDATE_INTERVAL);
        if (DEMO) {
            collectData(socket);
        } else {
            if (!pidsChecked) {
                while (!pidsChecked) {
                    checkAvailablePids(socket);
                }
            } else {
                collectData(socket);
            }
        }
    }

    private void fillAvailablePidsMap() {
        String[] pidsDescriptions = getBaseContext().getResources().getStringArray(R.array.pids_descriptions_mode1);
        int offset = 0;
        for (String string : pidsDescriptions) {
            availablePidsMap.put(string, new Pid(string, Helper.hexToBinary(String.valueOf(offset)), false));
            offset++;
            if(offset > 41)
                return;
        }
    }

    private BluetoothSocket connect(Intent intent) {
        if (DEMO)
            return null;
        deviceAddress = intent.getExtras().getString(DEVICE_ADDRESS_KEY, null);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(deviceAddress);
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
        return socket;
    }

    private void initializeOBDconnection(BluetoothSocket socket) throws IOException, InterruptedException {
        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
    }

    private void collectData(final BluetoothSocket socket) throws IOException, InterruptedException {
        final RPMCommand engineRpmCommand = new RPMCommand();
        final SpeedCommand speedCommand = new SpeedCommand();
        final EngineLoadCommand engineLoadCommand = new EngineLoadCommand();
        final CoolantTemperatureCommand coolantTemperatureCommand = new CoolantTemperatureCommand();
        final FuelLevelCommand fuelLevelCommand = new FuelLevelCommand();
        final FuelRateCommand fuelRateCommand = new FuelRateCommand();
        final OilTempCommand oilTempCommand = new OilTempCommand();
        final FuelRailAbsolutePressureCommand fuelRailAbsolutePressureCommand = new FuelRailAbsolutePressureCommand();
        final PidsSupportedCommand supportedPids = new PidsSupportedCommand(this, PidsSupportedCommand.Range.PIDS_01_20);

        try {
            engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
            speedCommand.run(socket.getInputStream(), socket.getOutputStream());
            engineLoadCommand.run(socket.getInputStream(), socket.getOutputStream());
            engineLoadCommand.run(socket.getInputStream(), socket.getOutputStream());
            coolantTemperatureCommand.run(socket.getInputStream(), socket.getOutputStream());
            oilTempCommand.run(socket.getInputStream(), socket.getOutputStream());
            fuelRailAbsolutePressureCommand.run(socket.getInputStream(), socket.getOutputStream());
            fuelRateCommand.run(socket.getInputStream(), socket.getOutputStream());
            supportedPids.run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
        }

        Log.d("TAG", "RPM: " + engineRpmCommand.getFormattedResult());
        Log.d("TAG", "fuelRate: " + fuelRateCommand.getFormattedResult());
        engineData.addCoolantTemperature(coolantTemperatureCommand.getFormattedResult())
                .addEngineLoad(engineLoadCommand.getFormattedResult())
                .addFuelLevel(fuelLevelCommand.getFormattedResult())
                .addFuelRailAbsolutePressure(fuelRailAbsolutePressureCommand.getFormattedResult())
                .addCoolantTemperature(coolantTemperatureCommand.getFormattedResult())
                .addRpm(engineRpmCommand.getFormattedResult())
                .addSupportedPids(supportedPids.getFormattedResult())
                .addFuelRate(fuelRateCommand.getFormattedResult())
                .addSpeed(speedCommand.getFormattedResult());
        engineData.setSupportedPids(availablePidsMap);
        Intent intent = new Intent("engineData");
        intent.putExtra(REFRESH_FRAME, engineData);
        sendBroadcast(intent);
    }

    private void checkAvailablePids(BluetoothSocket socket) throws IOException, InterruptedException {
        for (PidsSupportedCommand.Range range : PidsSupportedCommand.Range.values()) {
            try {
                PidsSupportedCommand pidsSupportedCommand = new PidsSupportedCommand(this, range);
                pidsSupportedCommand.run(socket.getInputStream(), socket.getOutputStream());
                availablePidsMap = pidsSupportedCommand.getResponse();
            } catch (NoDataException noDataException) {
                noDataException.printStackTrace();
            }
        }
        pidsChecked = true;
    }

    private void stopService() {
        serviceRunning = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        serviceRunning = false;
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        deviceAddress = intent.getExtras().getString(DEVICE_ADDRESS_KEY, null);
        return null;
    }

    @Override
    public void onCreate() {
        engineData = new EngineData();
        IntentFilter filter = new IntentFilter();
        filter.addAction("engineData");
    }

    @Override
    public void onDestroy() {
        serviceRunning = false;
    }
}
