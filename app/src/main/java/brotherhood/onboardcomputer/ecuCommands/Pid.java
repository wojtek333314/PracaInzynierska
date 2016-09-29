package brotherhood.onboardcomputer.ecuCommands;

import java.io.Serializable;
import java.util.ArrayList;

public class Pid implements Serializable {
    private String description;
    private String command;
    private String unit;
    private float value;
    private ArrayList<Float> values;
    private boolean available;

    public Pid(String description, String command, String unit, boolean available) {
        this.description = description;
        this.command = command;
        this.unit = unit;
        this.available = available;
        values = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public float getValue() {
        return value;
    }

    public ArrayList<Float> getValues() {
        return values;
    }

    public String getCommand() {
        return command;
    }

    public boolean isAvailable() {
        return available;
    }
}