package brotherhood.onboardcomputer.ecuCommands.mode4;

import com.github.pires.obd.commands.ObdCommand;

public class ClearErrorCodesCommand extends ObdCommand {


    public ClearErrorCodesCommand() {
        super("04");
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
