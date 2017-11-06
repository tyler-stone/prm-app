package io.tylerstone.prm.contact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.tylerstone.prm.MainActivity;
import io.tylerstone.prm.R;

public class ContactEditActivity extends AppCompatActivity {
    private static final String TAG = "ContactEditActivity";
    private EditText mName;
    private EditText mOccupation;
    private EditText mEmployer;
    private EditText mEmail;
    private EditText mPhone;
    private Button mDeleteButton;

    private EditStatus status;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);
        mName = (EditText) findViewById(R.id.contact_create_name_text);
        mOccupation = (EditText) findViewById(R.id.contact_create_occupation_text);
        mEmployer = (EditText) findViewById(R.id.contact_create_employer_text);
        mEmail = (EditText) findViewById(R.id.contact_create_email_text);
        mPhone = (EditText) findViewById(R.id.contact_create_phone_text);
        mDeleteButton = (Button) findViewById(R.id.contact_delete_button);

        status = (EditStatus) getIntent().getSerializableExtra("EDIT_STATUS");
        if (status == EditStatus.EDIT) {
            Long contactId = getIntent().getLongExtra("CONTACT_ID", -1);
            Log.d(TAG, Long.toString(contactId));
            contact = Contact.findById(Contact.class, contactId);

            mName.setText(contact.getName());
            mOccupation.setText(contact.getOccupation());
            mEmployer.setText(contact.getEmployer());
            mEmail.setText(contact.getEmail());
            mPhone.setText(contact.getPhone());
            mDeleteButton.setVisibility(View.VISIBLE);
        } else {
            contact = new Contact();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_discard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_save:
                contact.setName(mName.getText().toString());
                contact.setOccupation(mOccupation.getText().toString());
                contact.setEmployer(mEmployer.getText().toString());
                contact.setEmail(mEmail.getText().toString());
                contact.setPhone(mPhone.getText().toString());
                contact.save();

                finish();
                return true;
            case R.id.menu_discard:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("All unsaved changes will be lost.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        return;
    }

    public void deleteContact(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contact.delete();

                        Toast.makeText(getApplicationContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ContactEditActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        return;
    }

    public enum EditStatus {
        CREATE, EDIT
    }
}
