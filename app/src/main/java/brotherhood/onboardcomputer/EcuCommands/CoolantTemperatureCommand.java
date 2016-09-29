package brotherhood.onboardcomputer.ecuCommands;

/**
 * Created by Wojtas on 2016-09-13.
 */
public class CoolantTemperatureCommand extends Command{

    private int temperature = 0;

    public CoolantTemperatureCommand() {
        super("01 05");
    }

    @Override
    protected void performCalculations() {
        temperature = (buffer.get(2) - 40 );
    }

    @Override
    public String getFormattedResult() {
        return String.valueOf(temperature);
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
