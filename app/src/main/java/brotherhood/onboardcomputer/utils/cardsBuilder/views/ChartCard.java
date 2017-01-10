package brotherhood.onboardcomputer.utils.cardsBuilder.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Arrays;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.models.ChartModel;

public class ChartCard extends LinearLayout implements CardModel<ChartModel> {
    private static final int MAX_ENTRIES_ON_CHART = 100;
    private ChartModel chartModel;
    private Context context;
    private View view;
    private TextView name;
    private TextView temporaryValue;
    private ImageView status;
    private LineChart lineChartView;
    private MaterialRippleLayout rippleLayout;
    private OnClickListener onClickListener;

    public ChartCard(Context context) {
        super(context);
        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.chart_card, this, true);
        }

        name = (TextView) view.findViewById(R.id.pidName);
        status = (ImageView) view.findViewById(R.id.imageView_status);
        lineChartView = (LineChart) view.findViewById(R.id.lineChart);
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
        configLineChart();
    }

    public ChartCard(Context context, ChartModel chartModel) {
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

        lineChartView.setVisibility(data.isShowChart() ? VISIBLE : GONE);
        if (data.isShowChart()) {
            refreshLineChart(data);
        }

        status.setImageDrawable(data.getEngineCommand().isSupported() ?
                getResources().getDrawable(android.R.drawable.presence_online) :
                getResources().getDrawable(android.R.drawable.presence_offline));
        temporaryValue.setText(String.format("%s %s %s", context.getString(R.string.all_current_value),
                data.getEngineCommand().getLastValue(), data.getEngineCommand().getUnit()));
        rippleLayout.setOnClickListener(onClickListener);
    }

    private void refreshLineChart(ChartModel data){
        LineDataSet dataSet = new LineDataSet(Arrays.asList(data.getEngineCommand().getChartEntries()), "");
        while (dataSet.getEntryCount() > MAX_ENTRIES_ON_CHART) {
            dataSet.removeFirst();
        }
        dataSet.setDrawCircles(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setFillColor(getResources().getColor(R.color.white));
        dataSet.setColor(getResources().getColor(R.color.colorAccentRed));

        LineData lineData = new LineData(dataSet);
        lineData.setDrawValues(false);
        lineChartView.setData(lineData);
    }

    private void configLineChart() {
        lineChartView.getLegend().setEnabled(false);
        lineChartView.setDescription("");
        lineChartView.getAxisRight().setDrawLabels(false);
        lineChartView.getAxisRight().setGridColor(getResources().getColor(R.color.white));
        lineChartView.getXAxis().setDrawGridLines(false);
        lineChartView.getXAxis().setDrawAxisLine(false);
        lineChartView.getXAxis().setDrawLabels(false);
        lineChartView.getAxisLeft().setGridColor(getResources().getColor(R.color.white));
        lineChartView.getAxisLeft().setTextColor(getResources().getColor(R.color.white));
        lineChartView.getAxisLeft().setAxisLineColor(getResources().getColor(R.color.white));
        lineChartView.invalidate();
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
