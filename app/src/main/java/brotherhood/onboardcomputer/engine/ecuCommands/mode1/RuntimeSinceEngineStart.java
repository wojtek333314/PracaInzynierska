package brotherhood.onboardcomputer.engine.ecuCommands.mode1;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class RuntimeSinceEngineStart extends EngineCommand {

    public RuntimeSinceEngineStart() {
        super(1, 31, VisibilityMode.NORMAL_VIEW);
        setDescription("Runtime Since Engine Start");
        setUnit("sec.");
    }

    @Override
    protected void performCalculations() {
        addValue(Float.toString((buffer.get(2) * 256) + buffer.get(3)));
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
