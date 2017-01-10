package brotherhood.onboardcomputer.models;

import java.util.ArrayList;

public class RecordPidModel {
    private ArrayList<RecordPidValue> values;

    public RecordPidModel() {
        this.values = new ArrayList<>();
    }

    public ArrayList<RecordPidValue> getValues() {
        return values;
    }

    public RecordPidModel addData(RecordPidValue data) {
        this.values.add(data);
        return this;
    }
}
