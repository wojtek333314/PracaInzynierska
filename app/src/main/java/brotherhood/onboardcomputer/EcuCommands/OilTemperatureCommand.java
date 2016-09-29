package brotherhood.onboardcomputer.ecuCommands;

public class OilTemperatureCommand extends Command {

    private float oilTemperature = 0;

    public OilTemperatureCommand(Pid pid) {
        super(pid);
    }

    @Override
    protected void performCalculations() {
        oilTemperature = buffer.get(2) - 40;
    }

    @Override
    public String getFormattedResult() {
        return String.valueOf(oilTemperature);
    }

    @Override
    public String getCalculatedResult() {
        return null;
    }

    @Override
    public String getName() {
        return getPid().getDescription();
    }

    @Override
    protected String getUnit() {
        return getPid().getUnit();
    }
}
