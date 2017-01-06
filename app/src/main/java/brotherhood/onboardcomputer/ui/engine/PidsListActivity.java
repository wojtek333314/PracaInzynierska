package brotherhood.onboardcomputer.ui.engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engine.engineController.EngineController;

@EActivity(R.layout.pids_list_activity)
public class PidsListActivity extends FragmentActivity {
    public static final String DEVICE_ADDRESS_KEY = "address";

    @ViewById
    FrameLayout fragmentsContainer;

    private EngineController engineController;
    private EngineController.EngineListener engineListener;
    private PidsListFragment pidsListFragment;
    private TroubleCodesFragment troubleCodesFragment;
    private Fragment currentFragment;

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

    @Override
    public void onBackPressed() {
        if (currentFragment.equals(pidsListFragment)) {
            super.onBackPressed();
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
        troubleCodesFragment.setEngineController(engineController);
    }

    private void initEngineListener() {
        engineListener = new EngineController.EngineListener() {
            @Override
            public void onDataRefresh() {
                pidsListFragment.onDataRefresh();
            }
        };
    }

    private void initEngineController() {
        try {
            String engineBluetoothAddress = null;
            if (getIntent().hasExtra(DEVICE_ADDRESS_KEY)) {
                engineBluetoothAddress = getIntent().getExtras().getString(DEVICE_ADDRESS_KEY);
            }
            engineController = new EngineController(this, engineBluetoothAddress, engineListener);
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
}
