package brotherhood.onboardcomputer.data;

import brotherhood.onboardcomputer.ecuCommands.EngineCommand;

public class ChartModel {
    private EngineCommand engineCommand;

    public ChartModel(EngineCommand pid) {
        this.engineCommand = pid;
    }

    public EngineCommand getEngineCommand() {
        return engineCommand;
    }

    public boolean isShowChart() {
        return engineCommand.getVisibilityMode() == EngineCommand.VisibilityMode.CHART_VIEW;
    }
}
