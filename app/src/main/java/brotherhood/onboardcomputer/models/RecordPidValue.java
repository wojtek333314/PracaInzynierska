package brotherhood.onboardcomputer.models;

public class RecordPidValue {
    private String value;
    private long time;

    public RecordPidValue(String value, long time) {
        this.value = value;
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }
}
