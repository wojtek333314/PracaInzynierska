package brotherhood.onboardcomputer.EcuCommands;

import com.github.pires.obd.commands.ObdCommand;

/**
 * Created by Wojtas on 2016-09-13.
 */
public class PidsSupportedCommand extends ObdCommand {

    private String response;

    public PidsSupportedCommand(String command) {
        super(command);
    }

    @Override
    protected void performCalculations() {

    }

    @Override
    public String getFormattedResult() {
        return null;
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
