package brotherhood.onboardcomputer.utils.cardsBuilder.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.db.chart.view.LineChartView;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.data.ChartModel;
import brotherhood.onboardcomputer.utils.Helper;

public class ChartCard extends LinearLayout implements CardModel<ChartModel> {
    private ChartModel chartModel;
    private Context context;
    private View view;
    private OnClickListener onClickListener;
    private TextView name;
    private TextView temporaryValue;
    private ImageView status;
    private LineChartView lineChartView;
    private MaterialRippleLayout rippleLayout;

    public ChartCard(Context context) {
        super(context);
        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.chart_card, this, true);
        }

        name = (TextView) view.findViewById(R.id.textViewName);
        status = (ImageView) view.findViewById(R.id.imageView_status);
        lineChartView = (LineChartView) view.findViewById(R.id.lineChart);
        rippleLayout = (MaterialRippleLayout) view.findViewById(R.id.ripple);
        temporaryValue = (TextView) view.findViewById(R.id.textViewTemporaryValue);
    }

    public ChartCard(Context context, ChartModel chartModel) {
        this(context);
        this.chartModel = chartModel;
        refreshData(chartModel);
    }

    @Override
    public void refreshData(ChartModel data) {
        if (data == null)
            return;
        if (data.getPid().getDescription() != null)
            name.setText(data.getPid().getDescription());

        status.setImageDrawable(data.getPid().isSupported() ?
                getResources().getDrawable(android.R.drawable.presence_online) :
                getResources().getDrawable(android.R.drawable.presence_offline));
        temporaryValue.setText(String.format("%s %s", data.getPid().getValue(), data.getPid().getUnit()));
        lineChartView.setVisibility(GONE);//todo calculate!
        if (data.getPid().getDescription().equals("Engine RPM")) {
            System.out.println(data.getPid().getDescription() + "/" + data.getPid().getValue());
            Helper.showToast(context, data.getPid().getDescription() + "/" + data.getPid().getValue());
        }

        rippleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cv");
            }
        });
    }

    private String[] prepareLabels(int size) {
        String[] result = new String[size];
        for (int i = 0; i < size; i++)
            result[i] = "";
        return result;
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
