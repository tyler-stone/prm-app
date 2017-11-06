package io.tylerstone.prm.contact.list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.tylerstone.prm.R;
import io.tylerstone.prm.contact.Contact;
import io.tylerstone.prm.support.DateUtils;

/**
 * Created by Tyler on 11/21/2016.
 */

public class ContactListAdapter extends BaseAdapter {
    private static final int TYPE_CONTACT = 0;
    private static final int TYPE_HEADER = 1;
    private final Activity activity;
    private List<ListItem> list;
    private ArrayList<ContactListItemActionListener> contactListItemActionListeners;

    public ContactListAdapter(Activity activity, List<ListItem> list) {
        super();
        this.activity = activity;
        this.list = list;
        this.contactListItemActionListeners = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;
        LayoutInflater inflater = activity.getLayoutInflater();
        int rowType = getItemViewType(position);
        ListItem item = (ListItem) getItem(position);;

        switch(rowType) {
            case TYPE_CONTACT:
                rowView = inflater.inflate(R.layout.listitem_contact, null);

                viewHolder = new ViewHolder();
                viewHolder.view = item.getView(this, position, inflater, rowView);

                SwipeLayout mSwipeLayout;
                mSwipeLayout = (SwipeLayout) rowView.findViewById(R.id.listitem_contact_swipelayout);
                mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, rowView.findViewById(R.id.listitem_contact_swipeview_bottom));
                rowView.setTag(viewHolder);
                break;
            case TYPE_HEADER:
                rowView = inflater.inflate(R.layout.listitem_header, null);

                viewHolder = new ViewHolder();
                viewHolder.view = item.getView(this, position, inflater, rowView);
                rowView.setTag(viewHolder);
                break;
        }

        return rowView;
    }

    public void updateList(List<ListItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void reorganizeList() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stripLabels();

                //re-sort list of contacts by due date
                Collections.sort(list, new Comparator<ListItem>() {
                    public int compare(ListItem l1, ListItem l2) {
                        Contact c1 = ((ContactListItem) l1).getContact();
                        Contact c2 = ((ContactListItem) l2).getContact();

                        if (c1.getReminderDueDate() == null && c2.getReminderDueDate() != null) {
                            return -1;
                        } else if (c1.getReminderDueDate() != null && c2.getReminderDueDate() == null) {
                            return 1;
                        } else if (c1.getReminderDueDate() == null && c2.getReminderDueDate() == null) {
                            return 0;
                        } else {
                            return c1.getReminderDueDate().compareTo(c2.getReminderDueDate());
                        }
                    }
                });

                //re-add labels
                addLabels();
            }
        });
    }

    public void requestDataUpdate() {
        for (ContactListItemActionListener l : contactListItemActionListeners) {
            l.requestUpdate();
        }
    }

    public void notifyNoteAction(Contact contact) {
        for (ContactListItemActionListener l : contactListItemActionListeners) {
            l.contactNoteAction(contact);
        }
    }

    public void registerContactListItemActionListener(ContactListItemActionListener listener) {
        contactListItemActionListeners.add(listener);
    }

    public void unregisterContactListItemActionListener(ContactListItemActionListener listener) {
        contactListItemActionListeners.remove(listener);
    }

    private void stripLabels() {
        ArrayList<ListItem> toRemove = new ArrayList<>();
        for (int i = 0; i < this.getCount(); i++) {
            if (((ListItem) this.getItem(i)).getViewType() == TYPE_HEADER) {
                toRemove.add((ListItem) this.getItem(i));
            }
        }

        for (ListItem l : toRemove) {
            list.remove(l);
        }
    }

    private void addLabels() {
        long today = DateUtils.getToday();
        long tomorrow = DateUtils.getTomorrow();
        long nextDay = DateUtils.getNextDayFrom(new Date(tomorrow));
        long nextWeek = DateUtils.getNextWeekFrom(new Date());
        boolean pastDueHeaderPlaced = false;
        boolean todayHeaderPlaced = false;
        boolean tomorrowHeaderPlaced = false;
        boolean nextWeekHeaderPlaced = false;
        boolean laterHeaderPlaced = false;
        boolean notSetHeaderPlaced = false;

        for (int i = 0; i < this.getCount(); i++) {
            Contact contact = ((ContactListItem) this.getItem(i)).getContact();

            if (contact.getReminderDueDate() == null) {
                if (!notSetHeaderPlaced) {
                    list.add(i, new HeaderListItem("Not Set"));
                    notSetHeaderPlaced = true;
                }
            } else {
                long dueDate = contact.getReminderDueDate().getTime();
                if (dueDate < today && !pastDueHeaderPlaced) {
                    list.add(i, new HeaderListItem("Past Due"));
                    pastDueHeaderPlaced = true;
                } else if (dueDate >= today && dueDate < tomorrow && !todayHeaderPlaced) {
                    list.add(i, new HeaderListItem("Due Today"));
                    todayHeaderPlaced = true;
                } else if (dueDate >= tomorrow && dueDate < nextDay && !tomorrowHeaderPlaced) {
                    list.add(i, new HeaderListItem("Due Tomorrow"));
                    tomorrowHeaderPlaced = true;
                } else if (dueDate >= nextDay && dueDate <= nextWeek && !nextWeekHeaderPlaced) {
                    list.add(i, new HeaderListItem("Due Next Week"));
                    nextWeekHeaderPlaced = true;
                } else if (dueDate > nextWeek && !laterHeaderPlaced) {
                    list.add(i, new HeaderListItem("Due Later"));
                    laterHeaderPlaced = true;
                }
            }
        }
    }

    protected static class ViewHolder {
        public View view;
    }

    @Override
    public int getItemViewType(int position) {
        ListItem item = (ListItem) getItem(position);
        return item.getViewType();
    }
}

