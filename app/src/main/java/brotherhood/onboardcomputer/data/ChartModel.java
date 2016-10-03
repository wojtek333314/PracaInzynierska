package brotherhood.onboardcomputer.data;

import brotherhood.onboardcomputer.ecuCommands.Pid;

public class ChartModel {
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
}
