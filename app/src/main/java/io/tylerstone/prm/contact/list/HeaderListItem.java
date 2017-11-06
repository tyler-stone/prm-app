package io.tylerstone.prm.contact.list;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import io.tylerstone.prm.R;

/**
 * Created by tyler on 4/30/2017.
 */

public class HeaderListItem implements ListItem {
    private static final int TYPE_HEADER = 1;
    private String headerText;

    public HeaderListItem(String headerText) {
        this.headerText = headerText;
    }

    @Override
    public int getViewType() {
        return TYPE_HEADER;
    }

    @Override
    public View getView(ContactListAdapter adapter, int position, LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.listitem_header, null);
        } else {
            view = convertView;
        }

        TextView header_text = (TextView) view.findViewById(R.id.header_text);
        header_text.setText(headerText);

        return view;
    }
}
