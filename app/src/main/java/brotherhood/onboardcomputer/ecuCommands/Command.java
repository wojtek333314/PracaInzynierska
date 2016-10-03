package brotherhood.onboardcomputer.ecuCommands;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.exceptions.NoDataException;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import brotherhood.onboardcomputer.utils.Helper;

public class Command extends ObdCommand {
    private final static String PIDS_SUPPORTED = "PIDS_SUPPORTED";
    private final static String BIT_ENCODED = "BIT_ENCODED";
    private Pid pid;

    public Command(Pid pid) {
        super(pid.getCommand());
        this.pid = pid;
    }

    @Override
    protected void performCalculations() {
        if (pid.getCalculationsScript().equals("") || pid.getCalculationsScript().equals(PIDS_SUPPORTED)) {
            return;
        } else if (pid.getCalculationsScript().equals(BIT_ENCODED)) {
            String value = "";
            for (Integer integer : buffer) {
                value += Helper.hexToBinary(Integer.toHexString(integer));
            }
            pid.addValue(value);
            return;
        }

        Argument A = null;
        Argument B = null;
        if (pid.getCalculationsScript().contains("A") && buffer.size() > 1) {
            A = new Argument("A=" + Integer.toString(buffer.get(2)));
        }
        if (pid.getCalculationsScript().contains("B") && buffer.size() > 2) {
            B = new Argument("B=" + Integer.toString(buffer.get(3)));
        }
        Expression e = new Expression(pid.getCalculationsScript());
        if (A != null)
            e.addArguments(A);
        if (B != null)
            e.addArguments(B);
        e.addArguments();
        pid.addValue(String.valueOf(e.calculate()));
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
        if (getPid().isSupported()) {
            try {
                super.run(in, out);
            } catch (NoDataException noData) {
                noData.printStackTrace();
            }
        }
    }

    public Pid getPid() {
        return pid;
    }
}
