package brotherhood.onboardcomputer.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import brotherhood.onboardcomputer.R;

/**
 * Created by Wojtas on 2016-09-13.
 */
public class SimpleListAdapter extends BaseAdapter {
    private ArrayList<String> data;
    private Activity context;

    public SimpleListAdapter(Activity context, ArrayList<String> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row;
        row = inflater.inflate(R.layout.simple_list_item, parent, false);
        TextView textView;
        textView = (TextView) row.findViewById(R.id.textView);
        textView.setText(data.get(position));
        return (row);
    }
}
