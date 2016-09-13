package brotherhood.onboardcomputer.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.services.BluetoothConnectionService;
import brotherhood.onboardcomputer.services.EngineData;
import brotherhood.onboardcomputer.utils.SimpleListAdapter;
import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;

@EActivity(R.layout.choose_bluetooth)
public class DevicesListActivity extends Activity {

    @ViewById
    RadioGroup radioGroup;

    @ViewById
    Button connectButton;

    @ViewById
    ListView consoleListView;

    private BluetoothController bluetooth;
    private ArrayList<BluetoothDevice> list;
    private ArrayList<String> consoleList;
    private SimpleListAdapter consoleAdapter;
    private boolean getDataThreadRunning = true;
    private int atempt = 0;

    private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("engineData")) {
                EngineData engineData = ((EngineData) intent.getExtras().getSerializable(BluetoothConnectionService.REFRESH_FRAME));
                if (engineData != null) {
                    atempt++;
                    consoleList.clear();
                    consoleList.add(atempt
                            + "\n" + engineData.getLast(engineData.getRpm())
                            + "\n" + engineData.getLast(engineData.getSpeed())
                            + "\noil:" + engineData.getLast(engineData.getOilTemperature())
                            + "\nfuel rate:" + engineData.getLast(engineData.getFuelRate())
                            + "\ncoolant temp:" + engineData.getLast(engineData.getCoolantTemperature())
                            + "\nfuel pressure:" + engineData.getLast(engineData.getFuelRailAbsolutePressure()) + "\n---------------------");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        consoleAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    @AfterViews
    void afterView() {
        initList();
        initBluetooth();
        initButton();
        IntentFilter intentFilter = new IntentFilter("engineData");
        registerReceiver(serviceReceiver, intentFilter);
    }

    private void initList() {
        list = new ArrayList<>();
        consoleList = new ArrayList<>();
        consoleAdapter = new SimpleListAdapter(this, consoleList);
        consoleListView.setAdapter(consoleAdapter);
        consoleAdapter.notifyDataSetChanged();
    }

    @Click(R.id.sendButton)
    void findDevices() {
        getDataThreadRunning = !getDataThreadRunning;
    }

    void initButton() {
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedPosition = radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));
                if (checkedPosition < 0)
                    return;
                bluetooth.cancelScan();
                System.out.println(list.get(checkedPosition).getAddress());
                Toast.makeText(getApplicationContext(), list.get(checkedPosition).getAddress(), Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(DevicesListActivity.this, BluetoothConnectionService.class);
                serviceIntent.putExtra(BluetoothConnectionService.DEVICE_ADDRESS_KEY, list.get(checkedPosition).getAddress());
                startService(serviceIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.release();
    }

    private void initBluetooth() {
        bluetooth = BluetoothController.getInstance().build(this);
        bluetooth.setBluetoothListener(new BluetoothListener() {

            @Override
            public void onReadData(BluetoothDevice device, byte[] data) {
                System.out.println("read:" + String.valueOf(data));
            }

            @Override
            public void onActionStateChanged(int preState, int state) {
                System.out.println("onActionStateChanged" + state);
            }

            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {
                System.out.println("onActionDiscoveryStateChanged" + discoveryState);
            }

            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {
                System.out.println("onActionScanModeChanged:" + scanMode);
            }

            @Override
            public void onBluetoothServiceStateChanged(int state) {
                System.out.println("onBluetoothServiceStateChanged:" + state);
            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device, short rssi) {
                String text = device.getName() + "\n" + device.getAddress();
                boolean isOnList = false;
                for (BluetoothDevice item : list)
                    if (item.getName().equals(device.getName()) && item.getAddress().equals(device.getAddress()))
                        isOnList = true;

                if (!isOnList) {
                    RadioButton radioButton = new RadioButton(getApplicationContext());
                    radioButton.setText(text);
                    consoleList.add(text);
                    consoleAdapter.notifyDataSetChanged();
                    radioGroup.addView(radioButton);
                    list.add(device);
                }
            }
        });

        bluetooth.startScan();
    }
}
