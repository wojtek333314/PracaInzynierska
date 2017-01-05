package brotherhood.onboardcomputer.ui;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Random;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.data.TroubleCodeModel;
import brotherhood.onboardcomputer.engineController.EngineController;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.TroubleCodeCard;

@EFragment(R.layout.trouble_codes_fragment)
public class TroubleCodesFragment extends Fragment {
    @ViewById
    RecyclerView recyclerViewTroubleCodes;

    @ViewById
    TextView troubleCodesInfo;

    private EngineController engineController;
    private ArrayList<CardModel> codesCards;
    private CardsRecyclerViewAdapter cardsRecyclerViewAdapter;

    @AfterViews
    void initViews() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        if (codesCards != null) {
            recreateRecyclerView();
            return;
        }
        codesCards = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(10); i++) {
            codesCards.add(new TroubleCodeCard(getContext(), new TroubleCodeModel("Trouble P000" + i)));
        }
        recreateRecyclerView();
    }

    private void recreateRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerViewTroubleCodes.getContext());
        recyclerViewTroubleCodes.setLayoutManager(linearLayoutManager);
        cardsRecyclerViewAdapter = new CardsRecyclerViewAdapter(getActivity(), codesCards);
        recyclerViewTroubleCodes.setAdapter(cardsRecyclerViewAdapter);
        cardsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Click(R.id.deleteErrorsButton)
    void removeAllErrors() {

    }

    public TroubleCodesFragment setEngineController(EngineController engineController) {
        this.engineController = engineController;
        return this;
    }
}
