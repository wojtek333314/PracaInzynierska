package brotherhood.onboardcomputer.utils.cardsBuilder.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.models.SelectablePidModel;

public class SelectablePidCard extends LinearLayout implements CardModel<SelectablePidModel> {
    private Context context;
    private SelectablePidModel data;
    private View.OnClickListener onClickListener;
    private CheckBox checkBox;
    private View view;

    public SelectablePidCard(Context context, SelectablePidModel chartModel) {
        super(context);
        this.context = context;
        this.data = chartModel;
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setChecked(!data.isChecked());
                System.out.println("checked!"+data.getPidName()+":"+data.isChecked());
            }
        };
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.selectable_pid_card, this, true);
        }
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
    }

    @Override
    public void refreshData(SelectablePidModel data) {
        this.data = data;
        if(data!=null){
            checkBox.setText(data.getPidName());
            checkBox.setChecked(data.isChecked());
            checkBox.setOnClickListener(onClickListener);
        }

    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public SelectablePidModel getData() {
        return data;
    }

    @Override
    public Type getType() {
        return Type.SELECTABLE_CARD;
    }

    @Override
    public OnClickListener getClickListener() {
        return onClickListener;
    }
}
