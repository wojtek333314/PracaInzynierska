package brotherhood.onboardcomputer.utils.cardsBuilder.models;

public class TroubleCodeCardModel {
    private String code;

    public TroubleCodeCardModel(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public TroubleCodeCardModel setCode(String code) {
        this.code = code;
        return this;
    }
}
