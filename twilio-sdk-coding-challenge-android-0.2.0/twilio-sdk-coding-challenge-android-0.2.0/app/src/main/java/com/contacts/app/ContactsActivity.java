package com.contacts.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.contacts.ContactsManger;
import com.contacts.models.Contact;
import com.contacts.Contacts;
import com.contacts.utils.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this application to demonstrate the capabilities of the Contacts SDK you have implemented.
 * Specifically this app should contain the following features:
 *
 * 1. List all of a user's contacts.
 * 2. Add a contact
 * 3. Update the UI when a contact is updated.
 */

public class ContactsActivity extends AppCompatActivity {
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = ContactsManger.class.getName();
    public static final int CREATE_CONTACT_REQUEST_CODE = 1001;

    List<Contact> contacts = new ArrayList<>();
    ContactListAdapter adapter;
    RecyclerView contactList;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        init();

        // Lookup the recyclerview in activity layout
        contactList = (RecyclerView) findViewById(R.id.contactList);
        // Create adapter passing in the sample user data
        adapter = new ContactListAdapter(contacts);
        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactList.setLayoutManager(layoutManager);
        // Attach the adapter to the recyclerview to populate items
        contactList.setAdapter(adapter);
        // Divider
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        contactList.addItemDecoration(mDividerItemDecoration);

        // add floating action button click
        FloatingActionButton btn_add = findViewById(R.id.fab_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewContact();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Contacts.getInstance().syncContactData(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_CONTACT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String contactData = data.getStringExtra(Util.KEY_CONTACT_DATA);
            if (mIsDebuggable)
                Log.v(TAG, "New contact to be added: " + contactData);
            Contacts.getInstance().addNewContact(contactData);
        }
    }

    private void createNewContact() {
        Intent intent = new Intent(this, CreateContactActivity.class);
        startActivityForResult(intent, CREATE_CONTACT_REQUEST_CODE);
    }

    private void init() {
        Contacts.getInstance().setEventListener(new ContactsManger.ContactEventListener() {
            @Override
            public void onContactListLoaded() {
                contacts = Contacts.getInstance().getContactList();
                if (adapter != null) {
                    adapter.notifyDataChange(contacts);
                }
            }

            @Override
            public void onNewContactAdded(int responseCode) {
                if (responseCode == Util.HTTP_RESPONSE_SUCCESS) {
                    if (mIsDebuggable)
                        Log.v(TAG, "New contact successfully added in DB server. Updating local DB and UI");
                    Contacts.getInstance().onServerDataUpdated(getApplicationContext());
                } else {
                    if (mIsDebuggable)
                        Log.v(TAG, "New contact failed to be added in DB server. Error code: " + responseCode);
                }
            }

            @Override
            public void onContactUpdated(Contact oldContact, Contact newContact) {

            }
        });
    }
}
