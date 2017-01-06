package brotherhood.onboardcomputer.engine.ecuCommands.mode5;

import com.github.pires.obd.commands.ObdCommand;

public class GetErrorCodesCommand extends ObdCommand {
    public GetErrorCodesCommand(String command) {
        super("05 00");
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
