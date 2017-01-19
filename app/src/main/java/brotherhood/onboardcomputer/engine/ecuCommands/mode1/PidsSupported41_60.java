package brotherhood.onboardcomputer.engine.ecuCommands.mode1;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.engine.engineController.interfaces.CommandSupportedInterface;
import brotherhood.onboardcomputer.utils.ByteHelper;

public class PidsSupported41_60 extends EngineCommand implements CommandSupportedInterface {
    private boolean pidsAvailability[];

    public PidsSupported41_60() {
        super(1, 64, VisibilityMode.NONE);
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

    public PidsSupported41_60 enableDemo() {
        this.pidsAvailability = new boolean[32];
        for (int i = 0; i < pidsAvailability.length; i++) {
            pidsAvailability[i] = true;
        }
        return this;
    }

    @Override
    public boolean checkIsCommandSupported(EngineCommand engineCommand) {
        if(pidsAvailability == null || (pidsAvailability.length <= engineCommand.getPid()-64)){
            return false;
        }
        return engineCommand.getPid() >= 64 && pidsAvailability[engineCommand.getPid() - 65];
    }
}
