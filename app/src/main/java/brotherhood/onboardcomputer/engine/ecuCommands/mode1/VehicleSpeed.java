package brotherhood.onboardcomputer.engine.ecuCommands.mode1;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class VehicleSpeed extends EngineCommand {
    public VehicleSpeed() {
        super(1, 13, VisibilityMode.CHART_VIEW);
        setDescription("Vehicle Speed");
        setUnit("km/h");
    }

    @Override
    protected void performCalculations() {
        addValue(Float.toString(buffer.get(2)));
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
