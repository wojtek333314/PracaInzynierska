package brotherhood.onboardcomputer.ecuCommands;

import android.content.Context;

import com.github.pires.obd.commands.ObdCommand;

import java.util.LinkedHashMap;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.utils.Helper;

public class PidsSupportedCommand extends ObdCommand {
    private Context context;
    private static LinkedHashMap<String, Pid> response = new LinkedHashMap<>();
    private int offset = 0;

    public PidsSupportedCommand(Context context, Range range) {
        super(getCommand(range));
        this.context = context;

        for (int i = 0; i < Range.values().length; i++)
            if (Range.values()[i].equals(range))
                offset = i;
    }

    private static String getCommand(Range range) {
        switch (range) {
            case PIDS_01_20:
                return "01 00";
            case PIDS_21_40:
                return "01 20";
        }
        return "01 00";
    }

    @Override
    protected void performCalculations() {
        String value = "";
        for (Integer integer : buffer) {
            value += Helper.hexToBinary(Integer.toHexString(integer));
        }
        String descriptions[] = context.getResources().getStringArray(R.array.pids_descriptions_mode1);

        for (int i = 0; i < value.length(); i++) {
            int pos = i + offset * 20;
            response.put(descriptions[pos], new Pid(descriptions[pos], Helper.hexToBinary(String.valueOf(pos)), value.charAt(i) == '1'));
        }
    }

    public LinkedHashMap<String, Pid> getResponse() {
        return response;
    }

    @Override
    public String getFormattedResult() {
        String result = "";
        for (String key : response.keySet())
            result += key + "|" + String.valueOf(response.get(key));
        return result;
    }

    @Override
    public String getCalculatedResult() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public static boolean isCommandAvailable(Command command){

        return false;
    }

    public enum Range {
        PIDS_01_20,
        PIDS_21_40
    }


}
