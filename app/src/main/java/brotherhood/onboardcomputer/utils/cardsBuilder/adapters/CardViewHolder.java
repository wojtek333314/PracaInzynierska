package brotherhood.onboardcomputer.utils.cardsBuilder.adapters;


import android.support.v7.widget.RecyclerView;

import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;

public class CardViewHolder extends RecyclerView.ViewHolder {
    private CardModel<Object> cardModel;

    public CardViewHolder(CardModel cardModel) {
        super(cardModel.getView());
        this.cardModel = cardModel;
    }

    public void onBind(Object data) {
        if (data != null)
            cardModel.refreshData(data);
    }
}
