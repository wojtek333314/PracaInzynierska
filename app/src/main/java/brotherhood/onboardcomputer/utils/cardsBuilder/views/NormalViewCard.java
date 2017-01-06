package brotherhood.onboardcomputer.utils.cardsBuilder.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.models.ChartModel;

public class NormalViewCard extends LinearLayout implements CardModel<ChartModel> {
    private ChartModel chartModel;
    private Context context;
    private View view;
    private TextView name;
    private TextView temporaryValue;
    private ImageView status;
    private MaterialRippleLayout rippleLayout;
    private OnClickListener onClickListener;

    public NormalViewCard(Context context) {
        super(context);
        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.normal_card, this, true);
        }

        name = (TextView) view.findViewById(R.id.textViewName);
        status = (ImageView) view.findViewById(R.id.imageView_status);
        rippleLayout = (MaterialRippleLayout) view.findViewById(R.id.ripple);
        temporaryValue = (TextView) view.findViewById(R.id.textViewTemporaryValue);

        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getData() != null) {
                    refreshData(getData());
                }
            }
        };
    }

    public NormalViewCard(Context context, ChartModel chartModel) {
        this(context);
        this.chartModel = chartModel;
        refreshData(chartModel);
    }

    @Override
    public void refreshData(ChartModel data) {
        if (data == null) {
            return;
        }
        if (data.getEngineCommand().getDescription() != null) {
            name.setText(data.getEngineCommand().getDescription());
        }

        status.setImageDrawable(data.getEngineCommand().isSupported() ?
                getResources().getDrawable(android.R.drawable.presence_online) :
                getResources().getDrawable(android.R.drawable.presence_offline));
        temporaryValue.setText(String.format("%s %s %s", context.getString(R.string.all_current_value),
                data.getEngineCommand().getLastValue(), data.getEngineCommand().getUnit()));
        rippleLayout.setOnClickListener(onClickListener);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public ChartModel getData() {
        return chartModel;
    }

    @Override
    public Type getType() {
        return Type.CHART_CARD;
    }

    @Override
    public void setOnClickListener(OnClickListener clickListener) {
        super.setOnClickListener(clickListener);
        onClickListener = clickListener;
        rippleLayout.setOnClickListener(clickListener);
    }

    @Override
    public OnClickListener getClickListener() {
        return onClickListener;
    }

}
