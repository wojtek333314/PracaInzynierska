package brotherhood.onboardcomputer.ecuCommands.mode1;

import java.util.ArrayList;
import java.util.Arrays;

import brotherhood.onboardcomputer.ecuCommands.EngineCommand;

public class Mode1Controller {
    private PidsSupported01_20 pidsSupported01_20;
    private PidsSupported21_40 pidsSupported21_40;
    private EngineCommand[] engineCommands = new EngineCommand[]{
            new CoolantTemperature(), new EngineLoad(), new EngineRPM(), new FuelPressure(), new RuntimeSinceEngineStart(),
            new FuelPressure(), new ThrottlePosition(), new VehicleSpeed()
    };

    public Mode1Controller() {
        pidsSupported01_20 = new PidsSupported01_20();
        pidsSupported21_40 = new PidsSupported21_40();
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
            pidsSupported01_20.checkIsCommandSupported(engineCommand);
            pidsSupported21_40.checkIsCommandSupported(engineCommand);
        }
    }

    public PidsSupported01_20 getPidsSupported01_20() {
        return pidsSupported01_20;
    }

    public PidsSupported21_40 getPidsSupported21_40() {
        return pidsSupported21_40;
    }

    public void prepareDemoAvailability() {
        pidsSupported01_20.enableDemo();
        pidsSupported21_40.enableDemo();
    }
}
