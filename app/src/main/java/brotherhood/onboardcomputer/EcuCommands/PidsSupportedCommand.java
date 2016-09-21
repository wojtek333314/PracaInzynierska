package brotherhood.onboardcomputer.EcuCommands;

import android.content.Context;

import com.github.pires.obd.commands.ObdCommand;

import java.math.BigInteger;

import brotherhood.onboardcomputer.R;

/**
 * Created by Wojtas on 2016-09-13.
 */
public class PidsSupportedCommand extends ObdCommand {

    private final Range range;
    private String response = "";
    private Context context;
    private int offset = 0;

    public PidsSupportedCommand(Context context, Range range) {
        super(getCommand(range));
        this.context = context;
        this.range = range;
        initOffset();
    }

    private static String getCommand(Range range) {
        switch (range) {
            case PIDS_01_20:
                return "01 00";
            case PIDS_21_40:
                return "01 00";
            case PIDS_41_60:
                return "01 00";
            case PIDS_61_80:
                return "01 00";
            case PIDS_81_A0:
                return "01 00";
        }
        return "01 00";
    }

    private void initOffset() {
        for (int i = 0; i < Range.values().length; i++)
            if (Range.values()[i].equals(range))
                offset = i;
    }

    @Override
    protected void performCalculations() {
        response = "";
        for (Integer integer : buffer) {
            response += hexToBinary(Integer.toHexString(integer));
        }

        String descriptions[] = context.getResources().getStringArray(R.array.pids_descriptions);
        String result = "";
        System.out.println("of:"+offset);
        for (int i = 0; i < response.length(); i++) {
            System.out.println((descriptions[i + offset * 20] + " | " + response.charAt(i)));
            result += (descriptions[i + offset * 20] + " | " + response.charAt(i));
        }
        response = result;
    }

    public static String hexToBinary(String hex) {
        return new BigInteger(hex, 16).toString(2);
    }

    @Override
    public String getFormattedResult() {
        return response;
    }

    @Override
    public String getCalculatedResult() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public enum Range {
        PIDS_01_20,
        PIDS_21_40,
        PIDS_41_60,
        PIDS_61_80,
        PIDS_81_A0
    }
}
