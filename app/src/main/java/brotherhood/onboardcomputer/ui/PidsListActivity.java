package brotherhood.onboardcomputer.ui;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.data.ChartModel;
import brotherhood.onboardcomputer.ecuCommands.Pid;
import brotherhood.onboardcomputer.services.EngineController;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.ChartCard;
import brotherhood.onboardcomputer.views.dotsBackground.BackgroundView;

@EActivity(R.layout.pids_list_activity)
public class PidsListActivity extends Activity {
    private final static int MAX_TIME_VALUE_MS = 10000;
    private final static int SEEK_BAR_STEP = 500;
    public static final String DEVICE_ADDRESS_KEY = "address";

    @ViewById
    BackgroundView backgroundView;

    @ViewById
    SeekBar timeBar;

    @ViewById
    TextView timeBarValue;

    @ViewById
    RecyclerView recyclerView;

    private CardsRecyclerViewAdapter cardsRecyclerViewAdapter;
    private ArrayList<ChartModel> chartModels;
    private ArrayList<CardModel> cardsList;
    private HashMap<Pid, ChartModel> refreshMap = new HashMap<>();
    private EngineController engineController;
    private boolean availablePidsAddedToAdapter;

    @UiThread
    void refreshList() {
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @AfterViews
    void afterViews() {
        try {
            String engineBluetoothAddress = null;
            if (getIntent().hasExtra(DEVICE_ADDRESS_KEY)) {
                engineBluetoothAddress = getIntent().getExtras().getString(DEVICE_ADDRESS_KEY);
            }
            engineController = new EngineController(this, engineBluetoothAddress, new EngineController.EngineListener() {
                @Override
                public void onDataRefresh(ArrayList<Pid> pidsSupported) {
                    if (!availablePidsAddedToAdapter) {
                        initPidsList(pidsSupported);
                    } else {
                        refreshPidsData(pidsSupported);
                        refreshList();
                    }
                }
            });
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        initTimeBar();
        initRecyclerView();
    }

    @UiThread
    void initPidsList(ArrayList<Pid> pidsData) {
        for (Pid pid : pidsData) {
            ChartModel chartModel = new ChartModel(pid);
            chartModels.add(chartModel);
            chartModel.setPid(pid);
            ChartCard chartCard = new ChartCard(PidsListActivity.this, chartModel);
            cardsList.add(chartCard);
            refreshMap.put(pid, chartModel);
        }
        availablePidsAddedToAdapter = true;
    }

    @UiThread
    void refreshPidsData(ArrayList<Pid> pidsData) {
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        chartModels = new ArrayList<>();
        cardsList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        cardsRecyclerViewAdapter = new CardsRecyclerViewAdapter(this, cardsList);
        recyclerView.setAdapter(cardsRecyclerViewAdapter);
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initTimeBar() {
        timeBar.setMax(MAX_TIME_VALUE_MS);
        timeBar.incrementProgressBy(SEEK_BAR_STEP);
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 100) {
                    timeBarValue.setText(getString(R.string.real_time));
                } else {
                    timeBarValue.setText(String.format("%.2f%s", ((float) progress / 1000), "s"));
                }
                EngineController.UPDATE_INTERVAL = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onPause() {
        engineController.destroy();
        super.onPause();
    }
}
