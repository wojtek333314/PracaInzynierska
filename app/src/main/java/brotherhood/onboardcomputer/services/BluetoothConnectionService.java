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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import brotherhood.onboardcomputer.EcuCommands.CoolantTemperatureCommand;
import brotherhood.onboardcomputer.EcuCommands.EngineLoadCommand;
import brotherhood.onboardcomputer.EcuCommands.FuelRailAbsolutePressureCommand;
import brotherhood.onboardcomputer.EcuCommands.FuelRateCommand;
import brotherhood.onboardcomputer.EcuCommands.PidsSupportedCommand;

public class BluetoothConnectionService extends Service {
    public static int UPDATE_INTERVAL = 10;
    public static final String DEVICE_ADDRESS_KEY = "deviceAddress";
    public static final String REFRESH_FRAME = "refreshFrame";

    private static final String DEVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private Timer timer = new Timer();
    private String deviceAddress = null;
    private boolean serviceRunning = true;
    private EngineData engineData;
    private IntentFilter filter;

    public BluetoothConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        deviceAddress = intent.getExtras().getString(DEVICE_ADDRESS_KEY, null);
        return null;
    }

    @Override
    public void onCreate() {
        // Code to execute when the service is first created
        engineData = new EngineData();
        filter = new IntentFilter();
        filter.addAction("engineData");
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
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

        final BluetoothSocket finalSocket = socket;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    collectData(finalSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, UPDATE_INTERVAL);
        return START_STICKY;
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
     /*       engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
            speedCommand.run(socket.getInputStream(), socket.getOutputStream());
            engineLoadCommand.run(socket.getInputStream(), socket.getOutputStream());
            engineLoadCommand.run(socket.getInputStream(), socket.getOutputStream());
            coolantTemperatureCommand.run(socket.getInputStream(), socket.getOutputStream());
            oilTempCommand.run(socket.getInputStream(), socket.getOutputStream());
            fuelRailAbsolutePressureCommand.run(socket.getInputStream(), socket.getOutputStream());
            fuelRateCommand.run(socket.getInputStream(), socket.getOutputStream());*/
            supportedPids.run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
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
        Intent intent = new Intent("engineData");
        intent.putExtra(REFRESH_FRAME, engineData);
        sendBroadcast(intent);
    }

    private void stopService() {
        if (timer != null) timer.cancel();
        serviceRunning = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        serviceRunning = false;
        return super.onUnbind(intent);
    }
}
