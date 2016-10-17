package brotherhood.onboardcomputer.utils.cardsBuilder.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import brotherhood.onboardcomputer.R;

public class PhoneContactCard extends LinearLayout implements CardModel<String> {
    private Context context;
    private String data;
    private TextView contact;
    private View.OnClickListener onClickListener;
    private MaterialRippleLayout rippleLayout;
    private View view;

    public PhoneContactCard(Context context, String data, View.OnClickListener onClickListener) {
        super(context);
        this.context = context;
        this.data = data;
        this.onClickListener = onClickListener;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.phone_contact_card, this, true);
        }
        rippleLayout = (MaterialRippleLayout) view.findViewById(R.id.ripple);
        contact = (TextView) view.findViewById(R.id.contact_name);
        contact.setText(data);
    }

    public PhoneContactCard(Context context, String model) {
        this(context, model, null);
    }

    @Override
    public void refreshData(String data) {
        contact.setText(data);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public Type getType() {
        return Type.PHONE_CONTACT;
    }

    @Override
    public View.OnClickListener getClickListener() {
        return onClickListener;
    }

    @Override
    public void setOnClickListener(OnClickListener clickListener) {
        super.setOnClickListener(clickListener);
        onClickListener = clickListener;
        rippleLayout.setOnClickListener(clickListener);
    }
}
