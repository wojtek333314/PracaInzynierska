package brotherhood.onboardcomputer.ecuCommands;

import java.io.Serializable;

import brotherhood.onboardcomputer.data.ChartModel;

public class Pid implements Serializable {
    private String command;
    private String description;
    private String unit;
    private String calculationsScript;
    private String[] values;
    private boolean isSupported;

    public Pid(String command, String description, String calculationsScript, String unit, boolean isSupported) {
        this.command = command;
        this.description = description;
        this.unit = unit;
        this.calculationsScript = calculationsScript;
        this.isSupported = isSupported;
        values = new String[ChartModel.CHART_DATA_SIZE];
        for (int i = 0; i < values.length; i++) {
            values[i] = "0";
        }
    }

    public String getUnit() {
        return unit;
    }

    public String getCommand() {
        return command;
    }

    public String getCalculationsScript() {
        return calculationsScript;
    }

    public void addValue(String value) {
        System.arraycopy(values, 1, values, 0, values.length - 1);
        values[values.length - 1] = value;
    }

    public String getValue() {
        return values[values.length - 1];
    }

    public String[] getValues() {
        return values;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSupported() {
        return isSupported;
    }

    public Pid setSupported(boolean supported) {
        this.isSupported = supported;
        return this;
    }
}