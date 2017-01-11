package brotherhood.onboardcomputer.utils.cardsBuilder.models;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class SelectablePidCardModel {
    private boolean isChecked;
    private String pidName;
    private EngineCommand engineCommand;

    public SelectablePidCardModel(EngineCommand availableCommand, boolean isChecked) {
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

    public SelectablePidCardModel setChecked(boolean checked) {
        isChecked = checked;
        return this;
    }

    public String getPidName() {
        return pidName;
    }

    public SelectablePidCardModel setPidName(String pidName) {
        this.pidName = pidName;
        return this;
    }
}
