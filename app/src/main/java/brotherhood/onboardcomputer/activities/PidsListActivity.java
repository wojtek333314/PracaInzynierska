package brotherhood.onboardcomputer.activities;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.services.BluetoothConnectionService;
import brotherhood.onboardcomputer.views.dotsBackground.BackgroundView;

@EActivity(R.layout.pids_list_activity)
public class PidsListActivity extends Activity {
    private final static int MAX_TIME_VALUE_MS = 10000;
    private final static int SEEK_BAR_STEP = 500;

    @ViewById
    BackgroundView backgroundView;

    @ViewById
    SeekBar timeBar;

    @ViewById
    TextView timeBarValue;

    @AfterViews
    void afterViews() {
        initTimeBar();
    }

    private void initTimeBar() {
        timeBar.setMax(MAX_TIME_VALUE_MS);
        timeBar.incrementProgressBy(SEEK_BAR_STEP);
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress / 1000 < 1)
                    timeBarValue.setText(getString(R.string.real_time));
                else
                    timeBarValue.setText(String.valueOf(progress / 1000) + "s");

                System.out.println(progress);
                if (progress / 1000 > 0)
                    BluetoothConnectionService.UPDATE_INTERVAL = progress;
                else
                    BluetoothConnectionService.UPDATE_INTERVAL = 10;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
