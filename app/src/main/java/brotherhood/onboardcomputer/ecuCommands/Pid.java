package brotherhood.onboardcomputer.ecuCommands;

import java.io.Serializable;

public class Pid implements Serializable {
    private String description;
    private String command;
    private boolean available;

    public Pid(String description, String command, boolean available) {
        this.description = description;
        this.command = command;
        this.available = available;
    }

    public String getDescription() {
        return description;
    }

    public String getCommand() {
        return command;
    }

    public boolean isAvailable() {
        return available;
    }
}