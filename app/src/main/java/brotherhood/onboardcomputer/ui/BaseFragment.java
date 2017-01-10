package brotherhood.onboardcomputer.ui;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import brotherhood.onboardcomputer.R;


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

    protected void showDialog(String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", onClickListener);
        dialog.setInverseBackgroundForced(true);
        dialog.setCancelable(true);
        dialog.create().show();
    }
}
