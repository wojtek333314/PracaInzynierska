package brotherhood.onboardcomputer.data;

import java.util.ArrayList;

import brotherhood.onboardcomputer.services.EngineData;

public class ChartModel {
    private ArrayList<Float> chartValues;
    private String name;
    private boolean status;
    private EngineData engineData;

    public ChartModel(String name, EngineData engineData, boolean status) {
        this.name = name;
        this.status = status;
        this.engineData = engineData;
        chartValues = new ArrayList<>();
    }

    public void addValueToChart(float value) {
        chartValues.add(value);
    }

    public float[] getChartValues() {
        float result[] = new float[chartValues.size()];

        for (int i = 0; i < chartValues.size(); i++)
            result[i] = chartValues.get(i);
        return result;
    }

    public ChartModel setChartValues(ArrayList<Float> chartValues) {
        this.chartValues = chartValues;
        return this;
    }

    public String getName() {
        return name;
    }

    public ChartModel setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isStatusEnabled() {
        return status;
    }

    public ChartModel setStatus(boolean status) {
        this.status = status;
        return this;
    }
}
