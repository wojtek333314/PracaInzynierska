package brotherhood.onboardcomputer.utils.cardsBuilder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;



public class CardsRecyclerViewAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private ArrayList<CardModel> data = new ArrayList<>();
    private Context context;

    public CardsRecyclerViewAdapter(Context context, ArrayList<CardModel> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardViewHolder(CardModel.Type.getCardModel(context, CardModel.Type.getTypeByID(viewType), null));
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.onBind(data.get(position).getData());
        holder.itemView.setOnClickListener(data.get(position).getClickListener());
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType().getEnumID();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<CardModel> getData() {
        return data;
    }

    public void setData(ArrayList<CardModel> data) {
        this.data = new ArrayList<>();
        this.data.addAll(data);
    }
}
