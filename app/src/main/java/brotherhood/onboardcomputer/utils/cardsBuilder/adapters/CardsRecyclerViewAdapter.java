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
        holder.itemView.setOnClickListener(data.get(position).getClickListener());
        holder.onBind(data.get(position).getData());
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

    public void animateTo(ArrayList<CardModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    public void applyAndAnimateRemovals(ArrayList<CardModel> newModels) {
        for (int i = data.size() - 1; i >= 0; i--) {
            final CardModel model = data.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<CardModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final CardModel model = newModels.get(i);
            if (!data.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<CardModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final CardModel model = newModels.get(toPosition);
            final int fromPosition = data.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public CardModel removeItem(int position) {
        final CardModel model = data.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, CardModel model) {
        data.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final CardModel model = data.remove(fromPosition);
        data.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
