package brotherhood.onboardcomputer.engine.ecuCommands.mode1;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class ControleModuleVoltage extends EngineCommand {
    private float voltage;

    public ControleModuleVoltage() {
        super(1, 66, VisibilityMode.NORMAL_VIEW);
        setDescription("Controle Module Voltage");
        setUnit("V");
    }

    @Override
    protected void performCalculations() {
        int a = buffer.get(2);
        int b = buffer.get(3);
        voltage = (a * 256 + b) / 1000;
        addValue(String.valueOf(voltage));
    }

    @Override
    public String getFormattedResult() {
        return String.format("%f.2 %s", voltage, "[V]");
    }

    @Override
    public String getCalculatedResult() {
        return Float.toString(voltage);
    }

    @Override
    public String getName() {
        return getClass().getName();
    }
}
