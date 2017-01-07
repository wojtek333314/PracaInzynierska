package brotherhood.onboardcomputer.ui.engine;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Random;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engine.ecuCommands.mode3.GetErrorCodesCommand;
import brotherhood.onboardcomputer.engine.ecuCommands.mode4.ClearErrorCodesCommand;
import brotherhood.onboardcomputer.engine.engineController.EngineController;
import brotherhood.onboardcomputer.models.TroubleCodeModel;
import brotherhood.onboardcomputer.ui.BaseFragment;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.TroubleCodeCard;

@EFragment(R.layout.trouble_codes_fragment)
public class TroubleCodesFragment extends BaseFragment {
    private static final long DEMO_TIME_PAUSE = 1500;
    @ViewById
    RecyclerView recyclerViewTroubleCodes;

    @ViewById
    TextView troubleCodesInfo;

    @ViewById
    LinearLayout troubleCodesRefreshLayout;

    private EngineController engineController;
    private ArrayList<CardModel> codesCards;
    private CardsRecyclerViewAdapter cardsRecyclerViewAdapter;

    @AfterViews
    void initViews() {
        initRecyclerView();
        getTroubleCodes();
    }

    private void initRecyclerView() {
        if (codesCards != null) {
            recreateRecyclerView();
            return;
        }
        codesCards = new ArrayList<>();
        recreateRecyclerView();
    }

    private void recreateRecyclerView() {
        if (codesCards.size() == 0) {
            showInfo(getString(R.string.engine_trouble_codes_not_found));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerViewTroubleCodes.getContext());
        recyclerViewTroubleCodes.setLayoutManager(linearLayoutManager);
        cardsRecyclerViewAdapter = new CardsRecyclerViewAdapter(getActivity(), codesCards);
        recyclerViewTroubleCodes.setAdapter(cardsRecyclerViewAdapter);
        refreshList();
    }

    @Click(R.id.deleteErrorsButton)
    void removeAllErrors() {
        if (EngineController.DEMO) {
            startDemoClearThread();
        } else {
            ClearErrorCodesCommand clearErrorCodesCommand = new ClearErrorCodesCommand();
            engineController.runCommand(clearErrorCodesCommand, new EngineController.EngineListener() {
                @Override
                public void onDataRefresh() {
                    System.out.println("Trouble codes cleared!");
                    codesCards.clear();
                    refreshList();
                    getTroubleCodes();
                }
            });
        }

    }

    @UiThread
    void showInfo(String info) {
        troubleCodesInfo.setText(info);
        troubleCodesInfo.setVisibility(View.VISIBLE);
    }

    @UiThread
    void hideInfo() {
        troubleCodesInfo.setVisibility(View.GONE);
        YoYo.with(Techniques.BounceInDown).duration(500).playOn(recyclerViewTroubleCodes);
    }

    private void getTroubleCodes() {
        codesCards.clear();
        refreshList();

        showInfo(getString(R.string.engine_trouble_codes_searching));
        if (EngineController.DEMO) {
            getErrorsDemoThread();
        } else {
            final GetErrorCodesCommand getErrorCodesCommand = new GetErrorCodesCommand();
            engineController.runCommand(getErrorCodesCommand, new EngineController.EngineListener() {
                @Override
                public void onDataRefresh() {
                    hideInfo();
                    System.out.println("Error codes: " + getErrorCodesCommand.getFormattedResult());
                    showRefreshButton();
                }
            });
        }

    }

    @UiThread
    void getErrorsDemoThread() {
        final ArrayList<CardModel> preparedCards = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(10) + 1; i++) {
            preparedCards.add(new TroubleCodeCard(getContext(), new TroubleCodeModel("DemoTrouble P000" + (i + 1))));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DEMO_TIME_PAUSE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (isFragmentActive()) {
                        hideInfo();
                        codesCards.addAll(preparedCards);
                        refreshList();
                        showRefreshButton();
                    }

                }
            }
        }).start();
    }

    @UiThread
    void showRefreshButton() {
        troubleCodesRefreshLayout.setVisibility(View.VISIBLE);
    }

    @UiThread
    void startDemoClearThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isFragmentActive()) {
                    showInfo(getString(R.string.engine_trouble_codes_not_found));
                    codesCards.clear();
                    refreshList();
                }
            }
        }).start();
    }

    @UiThread
    void refreshList() {
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Click(R.id.troubleCodesRefreshButton)
    void refreshTroubleCodes() {
        getTroubleCodes();
    }

    public TroubleCodesFragment setEngineController(EngineController engineController) {
        this.engineController = engineController;
        return this;
    }
}
