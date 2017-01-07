package brotherhood.onboardcomputer.ui;

import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
    public BaseApplication getBaseApplication() {
        return ((BaseApplication) getApplication());
    }

    @Override
    protected void onResume() {
        getBaseApplication().applicationIsNotMinimized();
        super.onResume();
    }
}
