package brotherhood.onboardcomputer.engine.ecuCommands.mode1;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.interfaces.CommandSupportedInterface;
import brotherhood.onboardcomputer.utils.ByteHelper;

public class PidsSupported21_40  extends EngineCommand implements CommandSupportedInterface {
    private boolean pidsAvailability[];

    public PidsSupported21_40() {
        super(1, 20, VisibilityMode.NONE);
    }

    @Override
    protected void performCalculations() {
        pidsAvailability = ByteHelper.getAvailabilityArray(buffer);
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

    public PidsSupported21_40 enableDemo() {
        this.pidsAvailability = new boolean[32];
        for (int i = 0; i < pidsAvailability.length; i++) {
            pidsAvailability[i] = true;
        }
        return this;
    }

    @Override
    public boolean checkIsCommandSupported(EngineCommand engineCommand) {
        return engineCommand.getPid() > 32 && pidsAvailability[Integer.parseInt(Integer.toString(engineCommand.getPid()), 16) - 32];
    }
}