package io.tylerstone.prm.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.tylerstone.prm.R;
import io.tylerstone.prm.contact.list.SelectableContactListAdapter;

public class PhoneContactImportActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CONTACTS = 1;
    private static final String TAG = "PhoneContactImportActiv";
    private ListView mContactListView;
    private SelectableContactListAdapter mAdapter;
    private boolean contactsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact_import);

        mContactListView = (ListView) findViewById(R.id.list_phone_contacts);
        mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAdapter.notifyDataSetChanged();
                updateSelectedItemCount();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Contact Import")
                            .setMessage("Importing your contacts will speed up the onboarding process.")
                            .setPositiveButton("Ok, fine", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(PhoneContactImportActivity.this,
                                            new String[]{Manifest.permission.READ_CONTACTS},
                                            PERMISSIONS_REQUEST_CONTACTS);
                                }
                            })
                            .setNegativeButton("I don't care", null)
                            .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_CONTACTS);
            }
        } else {
            updateUI();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateSelectedItemCount() {
        TextView checkedItemCountTextView = (TextView) findViewById(R.id.contact_import_count_text);
        int itemCount = mContactListView.getCheckedItemCount();

        String checkedItemCountString = String.valueOf(itemCount);
        checkedItemCountString += (itemCount == 1 ? " contact to import." : " contacts to import.");
        checkedItemCountTextView.setText(checkedItemCountString);
    }

    private void updateUI() {
        new AsyncTask<Void, Void, ArrayList<Contact>>() {
            ProgressDialog dialog = new ProgressDialog(PhoneContactImportActivity.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Retrieving list of contacts...");
                dialog.show();
            }

            @Override
            protected ArrayList<Contact> doInBackground(Void... params) {
                ArrayList<Contact> contactList = new ArrayList<>();
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String phone = null;

                        if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);

                            pCur.moveToFirst();
                            phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            pCur.close();
                        }

                        Contact newContact = new Contact();
                        newContact.setPhoneBookId(Integer.parseInt(id));
                        newContact.setName(name);

                        if (phone != null) {
                            newContact.setPhone(phone);
                        }

                        contactList.add(newContact);
                    }
                }
                cur.close();
                return contactList;
            }

            @Override
            protected void onPostExecute(ArrayList<Contact> contactList) {
                if (mAdapter == null) {
                    mAdapter = new SelectableContactListAdapter(PhoneContactImportActivity.this,
                            contactList, mContactListView);
                    mContactListView.setAdapter(mAdapter);
                } else {
                    mAdapter.clear();
                    mAdapter.addAll(contactList);
                    mAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateUI();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    public void importContacts(View view) {
        SparseBooleanArray checkedContacts = mContactListView.getCheckedItemPositions();
        if (checkedContacts != null) {
            for (int i = 0; i < checkedContacts.size(); i++) {
                if (checkedContacts.valueAt(i)) {
                    Contact contact = (Contact) mContactListView.getAdapter().getItem(
                            checkedContacts.keyAt(i));
                    contact.save();
                }
            }
        }
        finish();
    }
}
