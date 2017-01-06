package brotherhood.onboardcomputer.models;

public class TroubleCodeModel {
    private String code;

    public TroubleCodeModel(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public TroubleCodeModel setCode(String code) {
        this.code = code;
        return this;
    }
}
