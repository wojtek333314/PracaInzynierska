package brotherhood.onboardcomputer.engine.ecuCommands.mode1;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class CoolantTemperature extends EngineCommand {

    public CoolantTemperature() {
        super(1, 5, VisibilityMode.CHART_VIEW);
        setDescription("Coolant temperature");
        setUnit("°C");
    }

    @Override
    protected void performCalculations() {
        addValue(Integer.toString(buffer.get(2) - 40));
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
