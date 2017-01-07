package brotherhood.onboardcomputer.ui;

import android.support.v4.app.Fragment;


public class BaseFragment extends Fragment {
    private boolean isActive;

    public boolean isFragmentActive() {
        return isActive;
    }

    @Override
    public void onResume() {
        isActive = true;
        super.onResume();
        System.out.println("Activated!");
    }

    @Override
    public void onPause() {
        isActive = false;
        super.onPause();
        System.out.println("Paused!");
    }

    public BaseFragment setActive(boolean active) {
        isActive = active;
        return this;
    }
}
