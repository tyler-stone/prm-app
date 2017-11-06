package io.tylerstone.prm.contact;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.orm.SugarContext;

import io.tylerstone.prm.R;
import uk.co.markormesher.android_fab.FloatingActionButton;

public class ContactViewActivity extends AppCompatActivity {
    private static final String TAG = "ContactViewActivity";
    private Long id;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.add);
        fab.setIcon(d);

        id = getIntent().getLongExtra("CONTACT_ID", -1);
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_edit:
                Intent intent = new Intent(ContactViewActivity.this, ContactEditActivity.class);
                intent.putExtra("CONTACT_ID", contact.getId());
                intent.putExtra("EDIT_STATUS", ContactEditActivity.EditStatus.EDIT);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        Log.d(TAG, Long.toString(id));
        contact = Contact.findById(Contact.class, id);

        if (contact != null) {
            TextView contactName = (TextView) findViewById(R.id.contact_name);
            contactName.setText(contact.getName());

            TextView contactEmployment = (TextView) findViewById(R.id.contact_employment);
            String employment = "";
            if (contact.getOccupation() != null && !(contact.getOccupation().isEmpty())) {
                employment = contact.getOccupation();
                if (contact.getEmployer() != null && !(contact.getEmployer().isEmpty())) {
                    employment += " at " + contact.getEmployer();
                }
            } else {
                employment = contact.getEmployer();
            }
            contactEmployment.setText(employment);

            TextView contactEmail = (TextView) findViewById(R.id.contact_email);
            if (contact.getEmail() == null || contact.getEmail().isEmpty()) {
                contactEmail.setText("None");
                contactEmail.setTypeface(null, Typeface.ITALIC);
            } else {
                contactEmail.setText(contact.getEmail());
            }

            TextView contactPhone = (TextView) findViewById(R.id.contact_phone);
            if (contact.getPhone() == null || contact.getPhone().isEmpty()) {
                contactPhone.setText("None");
                contactPhone.setTypeface(null, Typeface.ITALIC);
            } else {
                contactPhone.setText(contact.getPhone());
            }
        }
    }
}
