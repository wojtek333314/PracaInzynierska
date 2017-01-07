package brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class FuelPressure extends EngineCommand {
    public FuelPressure() {
        super(1, 10, VisibilityMode.NORMAL_VIEW);
        setDescription("Engine Gauge Pressure");
        setUnit("kPa");
    }

    @Override
    protected void performCalculations() {
        addValue(Float.toString((buffer.get(2) * 3)));
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
