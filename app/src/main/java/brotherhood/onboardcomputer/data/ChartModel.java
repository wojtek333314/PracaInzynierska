package brotherhood.onboardcomputer.data;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import brotherhood.onboardcomputer.ecuCommands.Pid;

public class ChartModel {
    private static int x;
    private boolean isEntriesAdding = false;
    private boolean showChart = false;
    private Pid pid;
    private List<Entry> entries = new ArrayList<>();

    public ChartModel(Pid pid) {
        this.pid = pid;
    }

    public Pid getPid() {
        return pid;
    }

    public ChartModel setPid(Pid pid) {
        this.pid = pid;
        if(isEntriesAdding){
            return this;
        }
        isEntriesAdding = true;
        entries.clear();
        for (String value : pid.getValues()) {
            entries.add(new Entry(x, Float.parseFloat(value)));
            x++;
        }
        isEntriesAdding = false;
        return this;
    }

    public List<Entry> getEntries() {
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
