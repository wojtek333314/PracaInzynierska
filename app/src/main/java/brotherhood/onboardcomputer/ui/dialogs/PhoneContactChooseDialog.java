package brotherhood.onboardcomputer.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.PhoneContactCard;

public class PhoneContactChooseDialog extends Dialog {
    private String contactChosen;
    private ArrayList<CardModel> cards = new ArrayList<>();
    private CardsRecyclerViewAdapter cardsRecyclerViewAdapter;
    private OnContactChooseListener onContactChooseListener;

    public PhoneContactChooseDialog(Context context, OnContactChooseListener onContactChooseListener) {
        super(context);
        onConstructor(context);
        this.onContactChooseListener = onContactChooseListener;
    }

    public PhoneContactChooseDialog(Context context, int themeResId) {
        super(context, themeResId);
        onConstructor(context);
    }

    protected PhoneContactChooseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        onConstructor(context);
    }

    private void onConstructor(Context context) {
        setContentView(R.layout.phone_contact_choose_dialog);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        cardsRecyclerViewAdapter = new CardsRecyclerViewAdapter(getContext(), cards);
        recyclerView.setAdapter(cardsRecyclerViewAdapter);
    }

    public PhoneContactChooseDialog show(final HashMap<String, String> contacts) {
        for(final String key : contacts.keySet()){
            cards.add(new PhoneContactCard(getContext(), contacts.get(key), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactChosen = contacts.get(key);
                    onContactChooseListener.onContactChoose(contactChosen);
                    dismiss();
                }
            }));
        }
        super.show();
        return this;
    }

    public interface OnContactChooseListener{
        void onContactChoose(String contactName);
    }
}
