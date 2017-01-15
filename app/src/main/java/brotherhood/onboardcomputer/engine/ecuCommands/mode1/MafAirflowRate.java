package brotherhood.onboardcomputer.engine.ecuCommands.mode1;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class MafAirflowRate extends EngineCommand {

    private float maf = -1.0f;

    public MafAirflowRate() {
        super(1, 16, VisibilityMode.CHART_VIEW);
        setUnit("g/s");
        setDescription("MAF Airflow rate");
    }

    @Override
    protected void performCalculations() {
        maf = (buffer.get(2) * 256 + buffer.get(3)) / 100.0f;
        addValue(String.valueOf(maf));
    }

    @Override
    public String getFormattedResult() {
        return String.valueOf(maf);
    }

    @Override
    public String getCalculatedResult() {
        return String.valueOf(maf);
    }

    @Override
    public String getName() {
        return getClass().getName();
    }
}
