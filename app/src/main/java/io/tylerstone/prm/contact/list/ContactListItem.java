package io.tylerstone.prm.contact.list;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import io.tylerstone.prm.PRM;
import io.tylerstone.prm.R;
import io.tylerstone.prm.contact.Contact;
import io.tylerstone.prm.contact.ReminderFrequency;

/**
 * Created by tyler on 3/26/2017.
 */

public class ContactListItem implements ListItem {
    private static final int TYPE_CONTACT = 0;
    Contact contact;

    public ContactListItem(Contact contact) {
        this.contact = contact;
    }

    @Override
    public int getViewType() {
        return TYPE_CONTACT;
    }

    @Override
    public View getView(final ContactListAdapter adapter, int position, LayoutInflater inflater, final View convertView) {
        final View view;
        final int objectPosition = position;
        final ContactListAdapter arrayAdapter = adapter;

        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.listitem_contact, null);
        } else {
            view = convertView;
        }

        final TextView contact_name = (TextView) view.findViewById(R.id.contact_name);
        TextView contact_occupation = (TextView) view.findViewById(R.id.contact_job_title);
        contact_name.setText(contact.getName());

        ImageButton flagButton = (ImageButton) view.findViewById(R.id.flag_interaction_btn);
        flagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "flag for " + contact.getName() , Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton snoozeButton = (ImageButton) view.findViewById(R.id.snooze_interaction_btn);
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact.snooze();
                Toast.makeText(PRM.getContext(), "Snoozed for " + ReminderFrequency.getReminderFromText(
                        contact.getReminderFrequency()).getReminderDurationText() + " from today", Toast.LENGTH_SHORT).show();

                adapter.requestDataUpdate();
            }
        });

        ImageButton noteButton = (ImageButton) view.findViewById(R.id.note_interaction_btn);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyNoteAction(contact);
            }
        });

        if (contact.getReminderDueDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
            contact_occupation.setText("Due: " + dateFormat.format(contact.getReminderDueDate()));
        } else {
            contact_occupation.setText("No Due Date");
        }

        return view;
    }


    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }


}
