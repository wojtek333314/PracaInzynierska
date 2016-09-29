package brotherhood.onboardcomputer.ecuCommands;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Command extends ObdCommand{
    public Command(String command) {
        super(command);
    }

    @Override
    protected void performCalculations() {

    }

    @Override
    public String getFormattedResult() {
        return null;
    }

    @Override
    public String getCalculatedResult() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
        if(PidsSupportedCommand.isCommandAvailable(this))
            super.run(in, out);
    }
}
