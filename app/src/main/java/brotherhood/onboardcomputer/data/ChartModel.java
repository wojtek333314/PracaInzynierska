package brotherhood.onboardcomputer.data;

import com.github.mikephil.charting.data.Entry;

import brotherhood.onboardcomputer.ecuCommands.Pid;

public class ChartModel {
    public static final int CHART_DATA_SIZE = 80;
    private static int x;
    private boolean isEntriesAdding = false;
    private boolean showChart = false;
    private Pid pid;
    private Entry entries[] = new Entry[CHART_DATA_SIZE];

    public ChartModel(Pid pid) {
        this.pid = pid;
    }

    public Pid getPid() {
        return pid;
    }

    public ChartModel setPid(Pid pid) {
        this.pid = pid;
        if (isEntriesAdding) {
            return this;
        }
        isEntriesAdding = true;
        int position = 0;
        for (String value : pid.getValues()) {
            entries[position] = new Entry(x, Float.parseFloat(value));
            position++;
            x++;
        }
        isEntriesAdding = false;
        return this;
    }

    public Entry[] getEntries() {
        return entries;
    }

    public boolean isShowChart() {
        return showChart;
    }

    public ChartModel setShowChart(boolean showChart) {
        this.showChart = showChart;
        return this;
    }
}
