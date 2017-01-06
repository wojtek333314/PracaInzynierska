package brotherhood.onboardcomputer.utils;

import java.util.ArrayList;

public class ByteHelper {

    public static boolean[] getAvailabilityArray(ArrayList<Integer> buffer) {
        System.out.println("-----------");
        String value = "";
        int pos = 0;
        for (Integer integer : buffer) {
            value += Helper.hexToBinary(Integer.toHexString(integer));
            System.out.println(Helper.hexToBinary(Integer.toHexString(integer)));
        }
        boolean result[] = new boolean[value.length()];

        System.out.println("++++++++++++++++++++");
        System.out.println(value);
        for (Character character : value.toCharArray()) {
            result[pos] = character == '1';
            pos++;
        }
        return result;
    }

}