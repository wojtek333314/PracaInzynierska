package brotherhood.onboardcomputer.EcuCommands;

import com.github.pires.obd.commands.ObdCommand;

/**
 * Created by Wojtas on 2016-09-13.
 */
public class OilTemperatureCommand extends ObdCommand {

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
