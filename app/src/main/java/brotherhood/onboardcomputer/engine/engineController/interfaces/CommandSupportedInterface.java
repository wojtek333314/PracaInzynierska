package brotherhood.onboardcomputer.engine.engineController.interfaces;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public interface CommandSupportedInterface {
    boolean checkIsCommandSupported(EngineCommand engineCommand);
}
