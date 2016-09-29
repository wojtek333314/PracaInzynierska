package brotherhood.onboardcomputer.ecuCommands;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Command extends ObdCommand {
    private Pid pid;

    public Command(Pid pid) {
        super(pid.getCommand());
        this.pid = pid;
    }

    protected abstract String getUnit();

    protected Pid getPid() {
        return pid;
    }

    @Override
    public void run(InputStream in, OutputStream out) {
        try {
            super.run(in, out);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
