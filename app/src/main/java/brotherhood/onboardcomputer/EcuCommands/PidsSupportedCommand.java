package brotherhood.onboardcomputer.ecuCommands;

import android.content.Context;

import com.github.pires.obd.commands.ObdCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import brotherhood.onboardcomputer.services.BluetoothConnectionService;
import brotherhood.onboardcomputer.utils.Helper;

public class PidsSupportedCommand extends ObdCommand {
    private Context context;
    private static ArrayList<Pid> response = new ArrayList<>();
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

        try {
            JSONArray jsonArray = new JSONArray(Helper.loadJSONFromAsset(context, "pids.json"));
            for (int i = 0; i < 20; i++) {
                int pos = i + offset * 20;
                JSONObject object = jsonArray.getJSONObject(pos);
                if (value.charAt(i) == '1' || BluetoothConnectionService.DEMO) {
                    response.add(new Pid(object.getString("command")
                            , object.getString("description")
                            , object.getString("calculationsScript")
                            , object.getString("unit")
                            , value.charAt(i) == '1'));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Pid> getResponse() {
        return response;
    }

    @Override
    public String getFormattedResult() {
        String result = "";
        for (Pid pid : response)
            result += pid.getDescription() + "|" + pid.isSupported();
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

    public enum Range {
        PIDS_01_20,
        PIDS_21_40
    }

}
