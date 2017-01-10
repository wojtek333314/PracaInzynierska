package brotherhood.onboardcomputer.models;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class SelectablePidModel {
    private boolean isChecked;
    private String pidName;
    private EngineCommand engineCommand;

    public SelectablePidModel(EngineCommand availableCommand, boolean isChecked) {
        this.engineCommand = availableCommand;
        this.pidName = availableCommand.getDescription();
        this.isChecked = isChecked;
    }

    public EngineCommand getEngineCommand() {
        return engineCommand;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public SelectablePidModel setChecked(boolean checked) {
        isChecked = checked;
        return this;
    }

    public String getPidName() {
        return pidName;
    }

    public SelectablePidModel setPidName(String pidName) {
        this.pidName = pidName;
        return this;
    }
}
