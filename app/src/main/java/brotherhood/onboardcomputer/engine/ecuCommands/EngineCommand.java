package brotherhood.onboardcomputer.engine.ecuCommands;

import com.github.mikephil.charting.data.Entry;
import com.github.pires.obd.commands.ObdCommand;

public abstract class EngineCommand extends ObdCommand {
    private final static int VALUES_BUFFER_SIZE = 25;
    private String description;
    private String unit;
    private Integer mode;
    private Integer pid;
    private VisibilityMode visibilityMode;
    private String[] values;
    private boolean isSupported;
    private Entry[] chartEntries;

    public EngineCommand(Integer mode, Integer pid, VisibilityMode visibilityMode) {
        super(convertModeAndPidToCommand(mode, pid));
        this.mode = mode;
        this.pid = pid;
        this.values = new String[VALUES_BUFFER_SIZE];
        this.chartEntries = new Entry[VALUES_BUFFER_SIZE];
        this.visibilityMode = visibilityMode;

        for (int i = 0; i < chartEntries.length; i++) {
            chartEntries[i] = new Entry();
            values[i] = "0";
        }
    }

    public String getDescription() {
        return description;
    }

    public EngineCommand setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public VisibilityMode getVisibilityMode() {
        return visibilityMode;
    }

    public EngineCommand setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Integer getMode() {
        return mode;
    }

    public Integer getPid() {
        return pid;
    }

    public boolean isSupported() {
        return isSupported;
    }

    public EngineCommand setSupported(boolean supported) {
        isSupported = supported;
        return this;
    }

    public String[] getValues() {
        return values;
    }

    public String getLastValue() {
        return values[values.length - 1];
    }

    public Entry[] getChartEntries() {
        return chartEntries;
    }

    public void addValue(String value) {
        System.arraycopy(values, 1, values, 0, values.length - 1);
        values[values.length - 1] = value;

        int i = 0;
        for (Entry entry : chartEntries) {
            entry.setY(Float.parseFloat(values[i]));
            entry.setX(i);
            i++;
        }
    }

    private static String convertModeAndPidToCommand(Integer mode, Integer pid) {
        String result = String.format("%02x", mode);
        result += " ";
        if (pid != null) {
            result += String.format("%02x", pid);
        }
        return result;
    }

    public enum VisibilityMode {
        CHART_VIEW,
        NORMAL_VIEW,
        NONE
    }
}
