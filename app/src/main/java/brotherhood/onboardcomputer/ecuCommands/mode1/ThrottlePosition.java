package brotherhood.onboardcomputer.ecuCommands.mode1;

import brotherhood.onboardcomputer.ecuCommands.EngineCommand;

public class ThrottlePosition extends EngineCommand {
    public ThrottlePosition() {
        super(1, 11, VisibilityMode.CHART_VIEW);
        setDescription("Throttle position");
        setUnit("%");
    }

    @Override
    protected void performCalculations() {
        addValue(Float.toString((buffer.get(2) * 100) / 255));
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
