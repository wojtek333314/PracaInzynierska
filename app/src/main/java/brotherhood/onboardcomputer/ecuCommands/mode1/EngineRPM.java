package brotherhood.onboardcomputer.ecuCommands.mode1;

import brotherhood.onboardcomputer.ecuCommands.EngineCommand;

public class EngineRPM extends EngineCommand {
    public EngineRPM() {
        super(1, 12, EngineCommand.VisibilityMode.CHART_VIEW);
        setDescription("Engine RPM");
        setUnit("rpm");
    }

    @Override
    protected void performCalculations() {
        addValue(Float.toString((buffer.get(2) * 256 + buffer.get(3)) / 4));
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
