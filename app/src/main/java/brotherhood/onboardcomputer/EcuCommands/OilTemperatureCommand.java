package brotherhood.onboardcomputer.ecuCommands;

public class OilTemperatureCommand extends Command {

    private float oilTemperature = 0;

    public OilTemperatureCommand() {
        super("01 5C");
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
        return null;
    }
}
