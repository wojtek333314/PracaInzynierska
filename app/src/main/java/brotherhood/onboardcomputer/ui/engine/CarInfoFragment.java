package brotherhood.onboardcomputer.ui.engine;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Random;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.ControleModuleVoltage;
import brotherhood.onboardcomputer.engine.ecuCommands.mode9.Vin;
import brotherhood.onboardcomputer.engine.engineController.EngineController;
import brotherhood.onboardcomputer.models.ChartModel;
import brotherhood.onboardcomputer.ui.BaseFragment;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.NormalViewCard;

@EFragment(R.layout.car_info_fragment)
public class CarInfoFragment extends BaseFragment {

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    TextView carInfoMessage;

    private EngineController engineController;
    private ArrayList<CardModel> cards;
    private CardsRecyclerViewAdapter adapter;
    private ControleModuleVoltage controleModuleVoltage;
    private Vin vinCommand;
    private Random random = new Random();

    private boolean dataIsCollected;
    private int dataCollectedCount;

    @AfterViews
    public void initViews() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        if (cards != null) {
            recreateRecyclerView();
            hideInfo();
            return;
        }
        cards = new ArrayList<>();
        recreateRecyclerView();
        initCards();
    }

    private void initCards() {
        controleModuleVoltage = new ControleModuleVoltage();
        vinCommand = new Vin();

        cards.add(new NormalViewCard(getContext(), new ChartModel(controleModuleVoltage)));
        cards.add(new NormalViewCard(getContext(), new ChartModel(vinCommand)));
        if (EngineController.DEMO) {
            controleModuleVoltage.addValue(String.valueOf(random.nextFloat() + 5));
            vinCommand.addValue("D0E0M0O1V1I1N123456");
            hideInfo();
        } else {
            recyclerView.setVisibility(View.GONE);
        }
        refreshList();
    }

    private void recreateRecyclerView() {
        if (cards.size() == 0) {
            showInfo(getString(R.string.all_collecting_data));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CardsRecyclerViewAdapter(getActivity(), cards);
        recyclerView.setAdapter(adapter);
        refreshList();
    }

    @UiThread
    public void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @UiThread
    public void showInfo(String string) {
        carInfoMessage.setText(string);
        carInfoMessage.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void hideInfo() {
        carInfoMessage.setVisibility(View.GONE);
        YoYo.with(Techniques.BounceInDown).duration(500).playOn(recyclerView);
    }

    public CarInfoFragment setEngineController(EngineController engineController) {
        this.engineController = engineController;
        return this;
    }

    @UiThread
    public void onDataRefresh() {
        if (!EngineController.DEMO && !dataIsCollected && isFragmentActive()) {

            if (engineController.getEngineCommandsController().checkIsCommandAvailable(controleModuleVoltage)) {
                engineController.addCommandToQueue(controleModuleVoltage, new EngineController.CommandListener() {
                    @Override
                    public void onDataRefresh() {
                        onDataCollected();
                    }

                    @Override
                    public void onNoData(EngineCommand engineCommand) {

                    }
                });
            }

            engineController.addCommandToQueue(vinCommand, new EngineController.CommandListener() {
                @Override
                public void onDataRefresh() {
                    onDataCollected();
                }

                @Override
                public void onNoData(EngineCommand engineCommand) {
                }
            });
        }
    }

    @UiThread
    public void onDataCollected() {
        dataCollectedCount++;
        if (dataCollectedCount == (cards.size() - 1)) {
            dataIsCollected = true;
            hideInfo();
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
