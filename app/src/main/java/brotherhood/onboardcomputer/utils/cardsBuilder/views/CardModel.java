package brotherhood.onboardcomputer.utils.cardsBuilder.views;

import android.content.Context;
import android.view.View;

import brotherhood.onboardcomputer.data.ChartModel;


public interface CardModel<D> {
    void refreshData(D data);

    View getView();

    D getData();

    Type getType();

    View.OnClickListener getClickListener();

    enum Type {
        CHART_CARD(0);


        private final int value;

        Type(int value) {
            this.value = value;
        }

        public static CardModel getCardModel(Context context, Type type, Object model) {
            switch (type) {
                case CHART_CARD:
                    return new ChartCard(context, (ChartModel) model);
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
