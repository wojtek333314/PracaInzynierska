package brotherhood.onboardcomputer.ecuCommands;

import com.github.pires.obd.commands.ObdCommand;

/**
 * Created by Wojtas on 2016-09-13.
 */
public class EngineLoadCommand extends ObdCommand {

    float engineLoadInPercents = 0;

    public EngineLoadCommand() {
        super("01 04");
    }

    @Override
    protected void performCalculations() {
        engineLoadInPercents = (buffer.get(2) * 100) / 255;
    }

    @Override
    public String getFormattedResult() {
        return String.valueOf(engineLoadInPercents);
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
