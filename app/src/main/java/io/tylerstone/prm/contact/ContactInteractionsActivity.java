package io.tylerstone.prm.contact;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import io.tylerstone.prm.R;
import io.tylerstone.prm.contact.interaction.Interaction;
import io.tylerstone.prm.contact.interaction.InteractionListAdapter;
import io.tylerstone.prm.contact.interaction.NoteInteraction;
import io.tylerstone.prm.support.TextEditorActivity;

public class ContactInteractionsActivity extends AppCompatActivity {
    private static final String TAG = "ContactInteractionsActi";
    private static final int NEW_NOTE_INTERACTION_ACTION = 1;
    private ListView mInteractionListView;
    private ScrollView scrollView;
    private InteractionListAdapter mAdapter;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_interactions);
        scrollView = (ScrollView) findViewById(R.id.interactions_scroll_view);
        mInteractionListView = (ListView) findViewById(R.id.list_interactions);

        Long contactId = getIntent().getLongExtra("CONTACT_ID", -1);
        Log.d(TAG, Long.toString(contactId));
        contact = Contact.findById(Contact.class, contactId);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(contact.getName());
        mInteractionListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        refreshInteractionsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_interactions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_reminder:
                intent = new Intent(ContactInteractionsActivity.this, ContactReminderActivity.class);
                intent.putExtra("CONTACT_ID", contact.getId());
                startActivity(intent);
                return true;
            case R.id.menu_user_details:
                intent = new Intent(ContactInteractionsActivity.this, ContactViewActivity.class);
                intent.putExtra("CONTACT_ID", contact.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void newNoteInteraction(View v) {
        Intent noteInteraction = new Intent(this, TextEditorActivity.class);
        startActivityForResult(noteInteraction, NEW_NOTE_INTERACTION_ACTION);
    }

    private void refreshInteractionsList() {
        List<Interaction> interactionList = new ArrayList<Interaction>(NoteInteraction.find(
                NoteInteraction.class, " contact = ? ", String.valueOf(contact.getId())));

        if (mAdapter == null) {
            mAdapter = new InteractionListAdapter(this, interactionList);
            mInteractionListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(interactionList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_NOTE_INTERACTION_ACTION) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.getString("result") != null) {
                    String result = extras.getString("result");
                    NoteInteraction note = new NoteInteraction(result, contact);
                    note.save();
                }
                refreshInteractionsList();
            }
        }
    }
}
