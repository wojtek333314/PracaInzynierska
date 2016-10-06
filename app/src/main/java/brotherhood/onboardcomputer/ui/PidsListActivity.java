package brotherhood.onboardcomputer.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.data.ChartModel;
import brotherhood.onboardcomputer.ecuCommands.Pid;
import brotherhood.onboardcomputer.services.BluetoothConnectionService;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.ChartCard;
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

    @ViewById
    RecyclerView recyclerView;

    private ArrayList<CardModel> pidsList;
    private CardsRecyclerViewAdapter cardsRecyclerViewAdapter;
    private boolean availablePidsAddedToAdapter;

    private ArrayList<ChartModel> chartModels;
    private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Pid> pidsData = BluetoothConnectionService.pidsSupported;
            if (pidsData != null) {
                if (!availablePidsAddedToAdapter) {
                    for (Pid pid : pidsData) {
                        ChartModel chartModel = new ChartModel(pid);
                        if (chartModel.getPid().isSupported()) {
                            chartModels.add(chartModel);
                            pidsList.add(new ChartCard(PidsListActivity.this, chartModel));
                        }
                    }
                    availablePidsAddedToAdapter = true;
                } else {
                    for (ChartModel chartModel : chartModels) {
                        for (Pid pid : pidsData) {
                            if (chartModel.getPid().getDescription().equals(pid.getDescription())) {
                                chartModel.setPid(pid);
                                break;
                            }
                        }
                    }
                }
                refreshList();
            }
        }
    };

    @UiThread
    void refreshList() {
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @AfterViews
    void afterViews() {
        initTimeBar();
        initRecyclerView();
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter("engineData");
        registerReceiver(serviceReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(serviceReceiver);
        super.onPause();
    }

    private void initRecyclerView() {
        pidsList = new ArrayList<>();
        chartModels = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        cardsRecyclerViewAdapter = new CardsRecyclerViewAdapter(this, pidsList);
        recyclerView.setAdapter(cardsRecyclerViewAdapter);
        cardsRecyclerViewAdapter.notifyDataSetChanged();
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
                    timeBarValue.setText(String.format("%s%s", String.valueOf(progress / 1000), "s"));

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
