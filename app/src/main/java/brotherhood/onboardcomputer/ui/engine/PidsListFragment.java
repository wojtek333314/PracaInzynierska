package brotherhood.onboardcomputer.ui.engine;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.engine.engineController.EngineController;
import brotherhood.onboardcomputer.models.ChartModel;
import brotherhood.onboardcomputer.ui.views.dotsBackground.BackgroundView;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.ChartCard;

@EFragment(R.layout.pids_list_fragment)
public class PidsListFragment extends Fragment implements EngineController.CommandListener {
    private final static int MAX_TIME_VALUE_MS = 2500;

    @ViewById
    BackgroundView backgroundView;

    @ViewById
    SeekBar timeBar;

    @ViewById
    TextView timeBarValue;

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    View engineOptionsLayout;

    @ViewById
    View loadingInfo;

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
        for (EngineCommand engineCommand : ((PidsListActivity) getActivity()).getEngineController()
                .getEngineCommandsController().getOnlyAvailableEngineCommands()) {
            ChartCard chartCard = new ChartCard(getActivity(), new ChartModel(engineCommand));
            cardsList.add(chartCard);
        }
        availablePidsAddedToAdapter = true;
        showEngineOptions();
    }

    private void showEngineOptions() {
        YoYo.with(Techniques.BounceInDown).duration(500).playOn(recyclerView);
        YoYo.with(Techniques.BounceInUp).duration(500).playOn(engineOptionsLayout);
        engineOptionsLayout.setVisibility(View.VISIBLE);
        hideLoadingInfo();
    }

    private void hideLoadingInfo() {
        YoYo.with(Techniques.FadeOutDown).duration(500).playOn(loadingInfo);
    }

    @UiThread
    void refreshPidsData() {
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        if (cardsList != null) {
            recreateRecyclerView();
            showEngineOptions();
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
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeBarValue.setText(String.format("%.2f%s", ((float) progress / 1000)
                        + ((float) EngineController.MIN_UPDATE_INTERVAL / 1000), "s"));
                EngineController.UPDATE_INTERVAL = progress + EngineController.MIN_UPDATE_INTERVAL;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        timeBarValue.setText(String.format("%.2f%s", ((float) timeBar.getProgress() / 1000)
                + ((float) EngineController.MIN_UPDATE_INTERVAL / 1000), "s"));
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

    @Override
    public void onNoData(EngineCommand engineCommand) {

    }

    @Click(R.id.troubleCodesButton)
    public void onTroubleCodesClick() {
        ((PidsListActivity) getActivity()).swapToTroubleCodesFragment();
    }

    @Click(R.id.chartsButton)
    public void chartsButtonClick() {
        ((PidsListActivity) getActivity()).swapToChartsRecorderFragment();
    }

    @Click(R.id.carInfoButton)
    public void carInfoButtonClick() {
        ((PidsListActivity) getActivity()).swapToCarInfoFragment();
    }
}
