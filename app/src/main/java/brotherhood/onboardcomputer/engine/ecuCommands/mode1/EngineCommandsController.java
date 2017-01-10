package brotherhood.onboardcomputer.engine.ecuCommands.mode1;

import java.util.ArrayList;
import java.util.Arrays;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.CoolantTemperature;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.EngineLoad;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.EngineRPM;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.PidsSupported01_20;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.PidsSupported21_40;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.PidsSupported41_60;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.RuntimeSinceEngineStart;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.ThrottlePosition;
import brotherhood.onboardcomputer.engine.ecuCommands.mode1.commands.VehicleSpeed;

public class EngineCommandsController {
    private PidsSupported01_20 pidsSupported01_20;
    private PidsSupported21_40 pidsSupported21_40;
    private PidsSupported41_60 pidsSupported41_60;

    private EngineCommand[] engineCommands = new EngineCommand[]{
            new CoolantTemperature(), new EngineLoad(), new EngineRPM(), new RuntimeSinceEngineStart(),
            new ThrottlePosition(), new VehicleSpeed()
    };

    public EngineCommandsController() {
        pidsSupported01_20 = new PidsSupported01_20();
        pidsSupported21_40 = new PidsSupported21_40();
        pidsSupported41_60 = new PidsSupported41_60();

    }

    public EngineCommand[] getEngineCommands() {
        return engineCommands;
    }

    public EngineCommand[] getOnlyAvailableEngineCommands() {
        ArrayList<EngineCommand> arrayList = new ArrayList<>(Arrays.asList(engineCommands));
        ArrayList<EngineCommand> availableEngineCommands = new ArrayList<>();
        for (EngineCommand engineCommand : arrayList) {
            if (engineCommand.isSupported()) {
                availableEngineCommands.add(engineCommand);
            }
        }
        EngineCommand[] result = new EngineCommand[availableEngineCommands.size()];
        return availableEngineCommands.toArray(result);
    }

    public void updatePidsAvailability() {
        for (EngineCommand engineCommand : engineCommands) {
            if (pidsSupported01_20.checkIsCommandSupported(engineCommand)) {
                engineCommand.setSupported(true);
            }
            if (pidsSupported21_40.checkIsCommandSupported(engineCommand)) {
                engineCommand.setSupported(true);
            }
            if(pidsSupported41_60.checkIsCommandSupported(engineCommand)){
                engineCommand.setSupported(true);
            }
        }
    }

    public boolean checkIsCommandAvailable(EngineCommand engineCommand) {
        return pidsSupported01_20.checkIsCommandSupported(engineCommand) || pidsSupported21_40.checkIsCommandSupported(engineCommand)
                || pidsSupported41_60.checkIsCommandSupported(engineCommand);
    }

    public PidsSupported01_20 getPidsSupported01_20() {
        return pidsSupported01_20;
    }

    public PidsSupported21_40 getPidsSupported21_40() {
        return pidsSupported21_40;
    }

    public PidsSupported41_60 getPidsSupported41_60() {
        return pidsSupported41_60;
    }

    public void prepareDemoAvailability() {
        pidsSupported01_20.enableDemo();
        pidsSupported21_40.enableDemo();
        pidsSupported41_60.enableDemo();
    }
}
