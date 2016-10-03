package brotherhood.onboardcomputer.ecuCommands;

import java.io.Serializable;
import java.util.ArrayList;

public class Pid implements Serializable {
    private String command;
    private String description;
    private String unit;
    private String calculationsScript;
    private ArrayList<String> values;
    private boolean isSupported;

    public Pid(String command, String description, String calculationsScript, String unit, boolean isSupported) {
        this.command = command;
        this.description = description;
        this.unit = unit;
        this.calculationsScript = calculationsScript;
        this.isSupported = isSupported;
        values = new ArrayList<>();
    }

    public String getUnit() {
        return unit;
    }

    public String getCommand() {
        return command;
    }

    public String getCalculationsScript() {
        return calculationsScript;
    }

    public void addValue(String value) {
        values.add(value);
    }

    public String getValue() {
        return values.size() > 0 ? values.get(values.size() - 1) : "0";
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSupported() {
        return isSupported;
    }

    public Pid setSupported(boolean supported) {
        this.isSupported = supported;
        return this;
    }
}