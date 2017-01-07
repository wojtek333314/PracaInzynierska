package brotherhood.onboardcomputer.engine.ecuCommands.mode4;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class ClearErrorCodesCommand extends EngineCommand {

    public ClearErrorCodesCommand() {
        super(4, 0, VisibilityMode.NONE);
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
