package brotherhood.onboardcomputer.engine.ecuCommands.trubleCodes;

import java.io.IOException;
import java.io.InputStream;

import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;

public class TroubleCodes extends EngineCommand {

    protected final static char[] DTC_PREFIX = {'P', 'C', 'B', 'U'};
    protected final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    protected StringBuilder result = null;

    public TroubleCodes() {
        super(3, null, VisibilityMode.NONE);
        result = new StringBuilder();
    }

    @Override
    protected void performCalculations() {
        final String result = getResult();
        String workingData;
        int startIndex = 0;//Header size.

        String canOneFrame = result.replaceAll("[\r\n]", "");
        int canOneFrameLength = canOneFrame.length();
        if (canOneFrameLength <= 16 && canOneFrameLength % 4 == 0) {//CAN(ISO-15765) protocol one frame.
            workingData = canOneFrame;//43yy{result}
            startIndex = 4;//Header is 43yy, yy showing the number of data items.
        } else if (result.contains(":")) {//CAN(ISO-15765) protocol two and more frames.
            workingData = result.replaceAll("[\r\n].:", "");//xxx43yy{result}
            startIndex = 7;//Header is xxx43yy, xxx is bytes of information to follow, yy showing the number of data items.
        } else {//ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = result.replaceAll("^43|[\r\n]43|[\r\n]", "");
        }
        for (int begin = startIndex; begin < workingData.length(); begin += 4) {
            String dtc = "";
            byte b1 = hexStringToByteArray(workingData.charAt(begin));
            int ch1 = ((b1 & 0xC0) >> 6);
            int ch2 = ((b1 & 0x30) >> 4);
            dtc += DTC_PREFIX[ch1];
            dtc += HEX_ARRAY[ch2];
            dtc += workingData.substring(begin + 1, begin + 4);
            if (dtc.equals("P0000")) {
                return;
            }
            this.result.append(dtc);
            this.result.append('\n');
        }
    }

    private byte hexStringToByteArray(char s) {
        return (byte) ((Character.digit(s, 16) << 4));
    }

    @Override
    public String getCalculatedResult() {
        return String.valueOf(result);
    }

    @Override
    protected void readRawData(InputStream in) throws IOException {
        byte b;
        StringBuilder res = new StringBuilder();
        char c;
        while (true) {
            b = (byte) in.read();
            if (b == -1) {
                break;
            }
            c = (char) b;
            if (c == '>') {
                break;
            }
            if (c != ' ') {
                res.append(c);
            }
        }
        rawData = res.toString().trim();
    }

    @Override
    public String getFormattedResult() {
        return result.toString();
    }

    @Override
    public String getName() {
        return getClass().getName();
    }
}
