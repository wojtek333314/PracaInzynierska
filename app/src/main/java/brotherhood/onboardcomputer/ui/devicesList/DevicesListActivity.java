package brotherhood.onboardcomputer.ui.devicesList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.ui.engine.PidsListActivity;
import brotherhood.onboardcomputer.ui.engine.PidsListFragment_;
import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;

@EActivity(R.layout.choose_bluetooth)
public class DevicesListActivity extends Activity {

    @ViewById
    RadioGroup radioGroup;

    @ViewById
    Button connectButton;

    private BluetoothController bluetooth;
    private ArrayList<BluetoothDevice> list;

    @AfterViews
    void afterView() {
        initList();
        initBluetooth();
        initButton();
    }

    private void initList() {
        list = new ArrayList<>();
    }

    void initButton() {

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedPosition = radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));
                if (checkedPosition < 0) {
                    connectButton.setText(getString(R.string.all_connect));
                    bluetooth.startScan();
                    return;
                }
                bluetooth.cancelScan();
                System.out.println(list.get(checkedPosition).getAddress());
                Toast.makeText(getApplicationContext(), list.get(checkedPosition).getAddress(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PidsListFragment_.class);
                intent.putExtra(PidsListActivity.DEVICE_ADDRESS_KEY, list.get(checkedPosition).getAddress());
                startActivity(intent);
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
                if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    connectButton.setText(getString(R.string.search));
                }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(discoveryState)){
                    connectButton.setText(getString(R.string.all_connect));
                    list.clear();
                }
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
                    radioGroup.addView(radioButton);
                    list.add(device);
                }
            }
        });
        bluetooth.startScan();
    }
}
