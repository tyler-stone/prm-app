package io.tylerstone.prm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.tylerstone.prm.contact.Contact;
import io.tylerstone.prm.contact.ContactEditActivity;
import io.tylerstone.prm.contact.ContactInteractionsActivity;
import io.tylerstone.prm.contact.list.ContactListAdapter;
import io.tylerstone.prm.contact.list.ContactListItem;
import io.tylerstone.prm.contact.list.ContactListItemActionListener;
import io.tylerstone.prm.contact.list.HeaderListItem;
import io.tylerstone.prm.contact.list.ListItem;
import io.tylerstone.prm.contact.PhoneContactImportActivity;
import io.tylerstone.prm.contact.interaction.NoteInteraction;
import io.tylerstone.prm.notification.NotificationEventReceiver;
import io.tylerstone.prm.support.DateUtils;
import io.tylerstone.prm.support.TextEditorActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int NEW_NOTE_INTERACTION_ACTION = 1;
    private ListView mContactListView;
    private ContactListAdapter mAdapter;
    private List<ListItem> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContactListView = (ListView) findViewById(R.id.list_contacts);
        mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItem selectedItem = (ListItem) adapterView.getAdapter().getItem(i);
                if (selectedItem instanceof ContactListItem) {
                    Contact contact = ((ContactListItem) selectedItem).getContact();
                    Intent intent = new Intent(MainActivity.this, ContactInteractionsActivity.class);
                    intent.putExtra("CONTACT_ID", contact.getId());
                    startActivity(intent);
                }
            }
        });

        updateUI();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactEditActivity.class);
                startActivity(intent);
            }
        });

        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);

//        if (BuildConfig.DEBUG) {
//            Intent intent = new Intent(MainActivity.this, PhoneContactImportActivity.class);
//            startActivity(intent);
//        } else {
            if (!previouslyStarted) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
                edit.commit();

                Intent intent = new Intent(MainActivity.this, PhoneContactImportActivity.class);
                startActivity(intent);
            }
//        }


        updateUI();
    }

    private List<ListItem> getSortedContacts() {
        contactList.clear();
        List<Contact> contacts = Contact.findWithQuery(Contact.class,
                "select * from Contact order by case when reminder_due_date is null then 1 else 0 end, reminder_due_date");

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

        for (Contact contact : contacts) {
            if (contact.getReminderDueDate() == null) {
                if (!notSetHeaderPlaced) {
                    contactList.add(new HeaderListItem("Not Set"));
                    notSetHeaderPlaced = true;
                }
            } else {
                long dueDate = contact.getReminderDueDate().getTime();
                if (dueDate < today && !pastDueHeaderPlaced) {
                    contactList.add(new HeaderListItem("Past Due"));
                    pastDueHeaderPlaced = true;
                } else if (dueDate >= today && dueDate < tomorrow && !todayHeaderPlaced) {
                    contactList.add(new HeaderListItem("Due Today"));
                    todayHeaderPlaced = true;
                } else if (dueDate >= tomorrow && dueDate < nextDay && !tomorrowHeaderPlaced) {
                    contactList.add(new HeaderListItem("Due Tomorrow"));
                    tomorrowHeaderPlaced = true;
                } else if (dueDate >= nextDay && dueDate <= nextWeek && !nextWeekHeaderPlaced) {
                    contactList.add(new HeaderListItem("Due Next Week"));
                    nextWeekHeaderPlaced = true;
                } else if (dueDate > nextWeek && !laterHeaderPlaced) {
                    contactList.add(new HeaderListItem("Due Later"));
                    laterHeaderPlaced = true;
                }
            }

            contactList.add(new ContactListItem(contact));
        }

        return contactList;
    }

    private void updateUI() {
            contactList = getSortedContacts();
            mAdapter = new ContactListAdapter(this, contactList);
            mAdapter.registerContactListItemActionListener(new ContactListItemActionListener() {
                @Override
                public void requestUpdate() {
                    List<ListItem> items = getSortedContacts();
                    mAdapter.updateList(items);
                }

                @Override
                public void contactNoteAction(Contact contact) {
                    Intent noteInteraction = new Intent(PRM.getContext(), TextEditorActivity.class);
                    noteInteraction.putExtra("CONTACT_ID", contact.getId());
                    startActivityForResult(noteInteraction, NEW_NOTE_INTERACTION_ACTION);
                }

                @Override
                public void contactFlagAction(Contact contact) {

                }
            });

            mContactListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_NOTE_INTERACTION_ACTION) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    String result = extras.getString("result");
                    long contactId = extras.getLong("CONTACT_ID", -1);

                    if (contactId != -1 && result != null) {
                        Contact contact = Contact.findById(Contact.class, contactId);
                        NoteInteraction note = new NoteInteraction(result, contact);
                        note.save();
                    }
                }
            }
        }
    }
}
