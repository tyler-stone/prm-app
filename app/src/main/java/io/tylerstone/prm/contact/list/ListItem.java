package io.tylerstone.prm.contact.list;

import android.view.LayoutInflater;
import android.view.View;

import io.tylerstone.prm.contact.list.ContactListAdapter;

/**
 * Created by tyler on 4/30/2017.
 */

public interface ListItem {
    public int getViewType();
    public View getView(ContactListAdapter adapter, int position, LayoutInflater inflater, View convertView);
}
