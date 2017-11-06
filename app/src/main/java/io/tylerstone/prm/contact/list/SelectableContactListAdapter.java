package io.tylerstone.prm.contact.list;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.tylerstone.prm.R;
import io.tylerstone.prm.contact.Contact;

/**
 * Created by Tyler on 1/2/2017.
 */

public class SelectableContactListAdapter extends ArrayAdapter {
    private final static String TAG = "SelectableContactListAd";
    private final Activity activity;
    private final List<Contact> list;
    private final ListView listView;

    public SelectableContactListAdapter(Activity activity, ArrayList<Contact> list, ListView listView) {
        super(activity, 0, list);
        this.activity = activity;
        this.list = list;
        this.listView = listView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder view;

        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listitem_contact_selectable, null);

            view = new ViewHolder();
            view.contact_name = (TextView) rowView.findViewById(R.id.contact_name);

            rowView.setTag(view);
        } else {
            CheckBox chk = (CheckBox) convertView.findViewById(R.id.checkBox);
            if(listView.isItemChecked(position)){
                chk.setChecked(true);
            } else {
                chk.setChecked(false);
            }
            view = (ViewHolder) rowView.getTag();
        }

        Contact item = list.get(position);
        view.contact_name.setText(item.getName());

        return rowView;
    }

    protected static class ViewHolder {
        protected TextView contact_name;
    }
}
