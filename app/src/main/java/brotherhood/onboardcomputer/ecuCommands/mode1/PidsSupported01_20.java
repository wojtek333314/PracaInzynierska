package brotherhood.onboardcomputer.ecuCommands.mode1;

import brotherhood.onboardcomputer.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.ecuCommands.mode1.interfaces.CommandSupportedInterface;
import brotherhood.onboardcomputer.utils.ByteHelper;

public class PidsSupported01_20 extends EngineCommand implements CommandSupportedInterface {
    private boolean pidsAvailability[];

    public PidsSupported01_20() {
        super(1, 0, VisibilityMode.NONE);
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

    public PidsSupported01_20 enableDemo() {
        this.pidsAvailability = new boolean[32];
        for (int i = 0; i < pidsAvailability.length; i++) {
            pidsAvailability[i] = true;
        }
        return this;
    }

    @Override
    public boolean checkIsCommandSupported(EngineCommand engineCommand) {
        return engineCommand.getPid() < 32 && pidsAvailability[Integer.parseInt(Integer.toHexString(engineCommand.getPid()), 16)];
    }
}
