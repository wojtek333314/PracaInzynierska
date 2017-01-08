package brotherhood.onboardcomputer.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import brotherhood.onboardcomputer.R;

public class InfoDialog extends AlertDialog {

    public InfoDialog(Context context, String message, View.OnClickListener onClickListener) {
        super(context);
        setContentView(R.layout.dialog_info);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);
        setTitle("Information");

        Button ok = (Button) findViewById(R.id.button);
        ok.setOnClickListener(onClickListener);
    }
}