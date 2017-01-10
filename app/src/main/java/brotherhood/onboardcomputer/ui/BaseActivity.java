package brotherhood.onboardcomputer.ui;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import brotherhood.onboardcomputer.R;

public class BaseActivity extends FragmentActivity {
    public BaseApplication getBaseApplication() {
        return ((BaseApplication) getApplication());
    }

    @Override
    protected void onResume() {
        getBaseApplication().applicationIsNotMinimized();
        super.onResume();
    }

    protected void showDialog(String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogTheme);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", onClickListener);
        dialog.setInverseBackgroundForced(true);
        dialog.setCancelable(true);
        dialog.create().show();
    }
}
