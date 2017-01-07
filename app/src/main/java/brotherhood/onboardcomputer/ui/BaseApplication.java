package brotherhood.onboardcomputer.ui;

import android.app.Application;

public class BaseApplication extends Application {


    private boolean minimized;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                minimized = true;
                break;
        }
    }


    public boolean isMinimized() {
        return minimized;
    }

    public void applicationIsNotMinimized() {
        minimized = false;
    }
}
