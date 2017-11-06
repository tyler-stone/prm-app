package io.tylerstone.prm.contact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import io.tylerstone.prm.R;

public class ContactReminderActivity extends AppCompatActivity {
    private static final String TAG = "ContactReminderActi";
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_reminder);

        Spinner reminderSpinner = (Spinner) findViewById(R.id.reminder_spinner);
        final TextView reminderDateTextView = (TextView) findViewById(R.id.reminder_date_text);
        final TextView reminderContextTextView = (TextView) findViewById(R.id.reminder_context_text);

        Long contactId = getIntent().getLongExtra("CONTACT_ID", -1);
        contact = Contact.findById(Contact.class, contactId);

        reminderContextTextView.setText("Remind you to contact " + contact.getName() + "...");

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_frequency_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner.setAdapter(spinnerAdapter);

        String reminderFrequency = contact.getReminderFrequency();
        if (reminderFrequency != null) {
            Log.d(TAG, "CURRENT: " + reminderFrequency);
            int spinnerPosition = spinnerAdapter.getPosition(reminderFrequency);
            reminderSpinner.setSelection(spinnerPosition);
        } else {
            reminderDateTextView.setText("Next reminder:" + ReminderFrequency.NEVER.toString());
        }

        reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String frequency = (String) parent.getSelectedItem();
                String currentFrequency = contact.getReminderFrequency();

                if (currentFrequency == null || !currentFrequency.equals(frequency)) {
                    Log.d(TAG, "FREQ SELECTED: " + frequency);
                    contact.setReminderFrequency(frequency);
                    contact.save();
                }

                if (contact.getReminderDueDate() != null) {
                    reminderDateTextView.setText("Next reminder: " + dateFormat.format(contact.getReminderDueDate()));
                } else {
                    reminderDateTextView.setText("Next reminder: " + ReminderFrequency.NEVER.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
