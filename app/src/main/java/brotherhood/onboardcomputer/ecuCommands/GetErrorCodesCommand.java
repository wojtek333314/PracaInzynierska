package brotherhood.onboardcomputer.ecuCommands;

import com.github.pires.obd.commands.ObdCommand;

public class GetErrorCodesCommand extends ObdCommand {
    public GetErrorCodesCommand(String command) {
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
