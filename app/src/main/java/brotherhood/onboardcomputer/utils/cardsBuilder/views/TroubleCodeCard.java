package brotherhood.onboardcomputer.utils.cardsBuilder.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.models.TroubleCodeModel;

public class TroubleCodeCard extends LinearLayout implements CardModel<TroubleCodeModel> {

    private Context context;
    private TroubleCodeModel model;
    private View.OnClickListener onClickListener;
    private View view;
    private TextView name;

    public TroubleCodeCard(Context context) {
        super(context);
        this.context = context;
        inflateView();
    }

    public TroubleCodeCard(Context context, TroubleCodeModel model) {
        super(context);
        this.context = context;
        this.model = model;
        inflateView();
    }

    private void inflateView() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.trouble_code_error, this, true);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        name = (TextView) view.findViewById(R.id.troubleCode);
    }

    @Override
    public void refreshData(TroubleCodeModel data) {
        if (view == null) {
            inflateView();
        }
        if (data == null) {
            return;
        }
        this.model = data;
        name.setText(data.getCode());
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public TroubleCodeModel getData() {
        return model;
    }

    @Override
    public Type getType() {
        return Type.TROUBLE_CODE_CARD;
    }

    @Override
    public View.OnClickListener getClickListener() {
        return onClickListener;
    }
}
