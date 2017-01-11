package brotherhood.onboardcomputer.utils.cardsBuilder.models;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class ChartCardModel {
    private EngineCommand engineCommand;

    public ChartCardModel(EngineCommand pid) {
        this.engineCommand = pid;
    }

    public EngineCommand getEngineCommand() {
        return engineCommand;
    }

    public boolean isShowChart() {
        return engineCommand.getVisibilityMode() == EngineCommand.VisibilityMode.CHART_VIEW;
    }
}
