package brotherhood.onboardcomputer.engine.ecuCommands.mode9;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class Vin extends EngineCommand {
    String vin = "";

    public Vin() {
        super(9, 2, VisibilityMode.NORMAL_VIEW);
        setUnit("");
        setDescription("VIN");
    }

    @Override
    protected void performCalculations() {
        final String result = getResult();
        String workingData;
        if (result.contains(":")) {//CAN(ISO-15765) protocol.
            workingData = result.replaceAll(".:", "").substring(9);//9 is xxx490201, xxx is bytes of information to follow.
            Matcher m = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE).matcher(convertHexToString(workingData));
            if(m.find()) workingData = result.replaceAll("0:49", "").replaceAll(".:", "");
        } else {//ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = result.replaceAll("49020.", "");
        }
        vin = convertHexToString(workingData).replaceAll("[\u0000-\u001f]", "");
        System.out.println("VIN:"+vin);
        addValue(vin);
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

    public String convertHexToString(String hex) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            stringBuilder.append((char) decimal);
        }
        return stringBuilder.toString();
    }
}
