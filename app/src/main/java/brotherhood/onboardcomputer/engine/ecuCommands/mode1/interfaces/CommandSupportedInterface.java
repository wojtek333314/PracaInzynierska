package brotherhood.onboardcomputer.engine.ecuCommands.mode1.interfaces;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public interface CommandSupportedInterface {
    boolean checkIsCommandSupported(EngineCommand engineCommand);
}
