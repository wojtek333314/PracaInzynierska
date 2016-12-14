package brotherhood.onboardcomputer.data;

import com.github.mikephil.charting.data.Entry;

import brotherhood.onboardcomputer.ecuCommands.Pid;

public class ChartModel {
    private boolean showChart = false;
    private Pid pid;

    public ChartModel(Pid pid) {
        this.pid = pid;
    }

    public Pid getPid() {
        return pid;
    }

    public ChartModel setPid(Pid pid) {
        this.pid = pid;
        return this;
    }

    public Entry[] getEntries() {
        return pid.getChartEntries();
    }

    public boolean isShowChart() {
        return showChart;
    }

    public ChartModel setShowChart(boolean showChart) {
        this.showChart = showChart;
        return this;
    }
}
