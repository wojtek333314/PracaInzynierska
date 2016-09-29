package brotherhood.onboardcomputer.ecuCommands;

public class FuelRateCommand extends Command {

    private int fuelRate = 0;

    public FuelRateCommand(Pid pid) {
        super(pid);
    }

    @Override
    protected void performCalculations() {
        fuelRate = (256 * buffer.get(2) + buffer.get(3)) / 20;
    }

    @Override
    public String getFormattedResult() {
        return String.valueOf(fuelRate);
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
