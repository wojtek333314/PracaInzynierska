package brotherhood.onboardcomputer.ecuCommands.mode1.interfaces;

import brotherhood.onboardcomputer.ecuCommands.EngineCommand;

public interface CommandSupportedInterface {
    boolean checkIsCommandSupported(EngineCommand engineCommand);
}
