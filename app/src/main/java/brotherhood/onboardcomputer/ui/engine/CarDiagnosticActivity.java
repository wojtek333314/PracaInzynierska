package brotherhood.onboardcomputer.ui.engine;

import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.engine.engineController.EngineController;
import brotherhood.onboardcomputer.ui.BaseActivity;

@EActivity(R.layout.pids_list_activity)
public class CarDiagnosticActivity extends BaseActivity {
    public static final String DEVICE_ADDRESS_KEY = "address";

    @ViewById
    FrameLayout fragmentsContainer;

    private EngineController engineController;
    private EngineController.CommandListener commandListener;
    private PidsListFragment pidsListFragment;
    private TroubleCodesFragment troubleCodesFragment;
    private CarInfoFragment carInfoFragment;
    private Fragment currentFragment;
    private ChartsRecorderFragment chartsRecorderFragment;

    @AfterViews
    void initialize() {
        initEngineListener();
        initEngineController();
        initFragments();
        swapToPidsListFragment();
    }

    public void swapToPidsListFragment() {
        changeFragment(pidsListFragment);
    }

    public void swapToTroubleCodesFragment() {
        changeFragment(troubleCodesFragment);
    }

    public void swapToCarInfoFragment() {
        changeFragment(carInfoFragment);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment.equals(pidsListFragment)) {
            if (chartsRecorderFragment.isRecording()) {
                showDialog(getString(R.string.stop_recording), null);
            } else {
                super.onBackPressed();
            }
        } else {
            swapToPidsListFragment();
        }
    }

    private void changeFragment(Fragment fragment) {
        if (fragment == currentFragment) {
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(fragmentsContainer.getId(), fragment).commit();
        currentFragment = fragment;
    }

    private void initFragments() {
        pidsListFragment = PidsListFragment_.builder().build();
        troubleCodesFragment = TroubleCodesFragment_.builder().build();
        carInfoFragment = CarInfoFragment_.builder().build();
        chartsRecorderFragment = ChartsRecorderFragment_.builder().build();
        chartsRecorderFragment.setEngineController(engineController);
        carInfoFragment.setEngineController(engineController);
        troubleCodesFragment.setEngineController(engineController);
    }

    private void initEngineListener() {
        commandListener = new EngineController.CommandListener() {
            @Override
            public void onDataRefresh() {
                pidsListFragment.onDataRefresh();
                carInfoFragment.onDataRefresh();
                chartsRecorderFragment.onDataRefresh();
            }

            @Override
            public void onNoData(EngineCommand engineCommand) {

            }
        };
    }

    private void initEngineController() {
        try {
            String engineBluetoothAddress = null;
            if (getIntent().hasExtra(DEVICE_ADDRESS_KEY)) {
                engineBluetoothAddress = getIntent().getExtras().getString(DEVICE_ADDRESS_KEY);
            }
            engineController = new EngineController(engineBluetoothAddress, commandListener);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public EngineController getEngineController() {
        return engineController;
    }

    @Override
    public void onPause() {
        engineController.destroy();
        super.onPause();
    }

    public void swapToChartsRecorderFragment() {
        changeFragment(chartsRecorderFragment);
    }
}
