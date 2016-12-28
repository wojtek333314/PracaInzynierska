package brotherhood.onboardcomputer.ecuCommands.mode1;

import brotherhood.onboardcomputer.ecuCommands.EngineCommand;

public class EngineLoad extends EngineCommand {
    public EngineLoad() {
        super(1, 4, EngineCommand.VisibilityMode.CHART_VIEW);
        setDescription("Engine load");
        setUnit("%");
    }

    @Override
    protected void performCalculations() {
        addValue(Float.toString(buffer.get(2) / 2.55f));
    }

    @Override
    public String getFormattedResult() {
        return getLastValue() + " " + getUnit();
    }

    @Override
    public String getCalculatedResult() {
        return getLastValue();
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
