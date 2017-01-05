package brotherhood.onboardcomputer.ui;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.data.ChartModel;
import brotherhood.onboardcomputer.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.engineController.EngineController;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.ChartCard;
import brotherhood.onboardcomputer.views.dotsBackground.BackgroundView;

@EFragment(R.layout.pids_list_fragment)
public class PidsListFragment extends Fragment implements EngineController.EngineListener {
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

    private CardsRecyclerViewAdapter cardsRecyclerViewAdapter;
    private ArrayList<CardModel> cardsList;
    private boolean availablePidsAddedToAdapter;

    @UiThread
    void refreshList() {
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @AfterViews
    void afterViews() {
        initTimeBar();
        initRecyclerView();
    }

    @UiThread
    void initPidsList() {
        for (EngineCommand engineCommand : ((PidsListActivity) getActivity()).getEngineController().getMode1Controller().getOnlyAvailableEngineCommands()) {
            ChartModel chartModel = new ChartModel(engineCommand);
            ChartCard chartCard = new ChartCard(getActivity(), chartModel);
            cardsList.add(chartCard);
        }
        availablePidsAddedToAdapter = true;
    }

    @UiThread
    void refreshPidsData() {
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        if (cardsList != null) {
            recreateRecyclerView();
            return;
        }
        cardsList = new ArrayList<>();
        recreateRecyclerView();
    }

    private void recreateRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        cardsRecyclerViewAdapter = new CardsRecyclerViewAdapter(getActivity(), cardsList);
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
    public void onDataRefresh() {
        if (!availablePidsAddedToAdapter) {
            initPidsList();
        } else {
            refreshPidsData();
            refreshList();
        }
    }

    @Click(R.id.troubleCodesButton)
    public void onTroubleCodesClick() {
        ((PidsListActivity) getActivity()).swapToTroubleCodesFragment();
    }

    @Click(R.id.compareWithOthersButton)
    public void compareWithOthersClick() {

    }

    @Click(R.id.engineTestsButton)
    public void engineTestsClick() {

    }

}
