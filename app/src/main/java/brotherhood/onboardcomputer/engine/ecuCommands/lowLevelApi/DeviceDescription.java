package brotherhood.onboardcomputer.engine.ecuCommands.lowLevelApi;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class DeviceDescription extends EngineCommand {

    private String description;

    public DeviceDescription() {
        super("@1");
        setUnit("");
        setDescription("Adapter description");
    }

    @Override
    protected void performCalculations() {
        for (Integer integer : buffer) {
            description += Character.toString((char) ((int) integer));
        }
    }

    @Override
    public String getFormattedResult() {
        return description;
    }

    @Override
    public String getCalculatedResult() {
        return description;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }
}
