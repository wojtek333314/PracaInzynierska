package brotherhood.onboardcomputer.engine.ecuCommands;

public class TestClass {


    public static void main(String[] args){
        System.out.println(convertModeAndPidToCommand(1,10));
    }

    private static String convertModeAndPidToCommand(Integer mode, Integer pid) {
        String result = String.format("%02x", mode);
        result += " ";
        if (pid != null) {
            result += String.format("%02x", pid);
        }
        return result;
    }

}
