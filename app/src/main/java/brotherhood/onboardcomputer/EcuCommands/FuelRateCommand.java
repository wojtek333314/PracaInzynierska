package brotherhood.onboardcomputer.EcuCommands;

import com.github.pires.obd.commands.ObdCommand;

/**
 * Created by Wojtas on 2016-09-13.
 */
public class FuelRateCommand extends ObdCommand {

    private int fuelRate = 0;

    public FuelRateCommand() {
        super("01 5E");
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
        return null;
    }
}
