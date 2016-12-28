package brotherhood.onboardcomputer.utils;

import java.util.ArrayList;

public class ByteHelper {

    public static boolean[] getAvailabilityArray(ArrayList<Integer> buffer) {
        boolean result[] = new boolean[buffer.size()];

        String value = "";
        int pos = 0;
        for (Integer integer : buffer) {
            value += Helper.hexToBinary(Integer.toHexString(integer));
        }

        for (Character character : value.toCharArray()) {
            result[pos] = character == '1';
            pos++;
        }
        return result;
    }

}
