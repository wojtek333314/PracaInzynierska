package brotherhood.onboardcomputer.EcuCommands;

import com.github.pires.obd.commands.ObdCommand;

/**
 * Created by Wojtas on 2016-09-13.
 */
public class FuelRailAbsolutePressureCommand extends ObdCommand {
    private float kPaPressure = 0;

    public FuelRailAbsolutePressureCommand() {
        super("01 59");
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
        return null;
    }
}
