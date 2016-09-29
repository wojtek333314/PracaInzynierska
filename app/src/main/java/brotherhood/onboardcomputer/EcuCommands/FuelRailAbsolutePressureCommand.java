package brotherhood.onboardcomputer.ecuCommands;

public class FuelRailAbsolutePressureCommand extends Command {
    private float kPaPressure = 0;

    public FuelRailAbsolutePressureCommand(Pid pid) {
        super(pid);
    }

    @Override
    protected void performCalculations() {
        kPaPressure = 10 * (256 * buffer.get(2) + buffer.get(3));
    }

    @Override
    public String getFormattedResult() {
        return String.valueOf(kPaPressure);
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
