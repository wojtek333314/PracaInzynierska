package brotherhood.onboardcomputer.utils.cardsBuilder.views;

import android.content.Context;
import android.view.View;

import brotherhood.onboardcomputer.models.ChartModel;
import brotherhood.onboardcomputer.models.TroubleCodeModel;


public interface CardModel<D> {
    void refreshData(D data);

    View getView();

    D getData();

    Type getType();

    View.OnClickListener getClickListener();

    enum Type {
        CHART_CARD(0),
        PHONE_CONTACT(1),
        TROUBLE_CODE_CARD(2), NORMAL_VIEW_CARD(3);


        private final int value;

        Type(int value) {
            this.value = value;
        }

        public static CardModel getCardModel(Context context, Type type, Object model) {
            switch (type) {
                case CHART_CARD:
                    return new ChartCard(context, (ChartModel) model);
                case PHONE_CONTACT:
                    return new PhoneContactCard(context, (String) model);
                case TROUBLE_CODE_CARD:
                    return new TroubleCodeCard(context, (TroubleCodeModel) model);
                case NORMAL_VIEW_CARD:
                    return new NormalViewCard(context, (ChartModel) model);
            }
            return null;
        }

        public static Type getTypeByID(int id) {
            for (Type type : Type.values())
                if (id == type.getEnumID())
                    return type;

            return null;
        }

        public static int getID(Type type) {
            for (int i = 0; i < Type.values().length; i++)
                if (Type.values()[i] == type)
                    return i;
            return -1;
        }

        public int getEnumID() {
            return value;
        }
    }
}
