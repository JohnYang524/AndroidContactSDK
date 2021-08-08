package com.contacts.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.contacts.Contact;
import com.contacts.Contacts;
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
        // Initialize contacts
//        contacts = createTestList();
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
                Toast.makeText(getApplicationContext(), Contacts.getInstance().getContactListFromServer(), Toast.LENGTH_SHORT).show();
                createNewContact();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Contacts.getInstance().syncContactList(this);
    }

    private void createNewContact() {
        Intent intent = new Intent(this, CreateContactActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private List<Contact> createTestList() {
        List<Contact> testList = new ArrayList<>();
        testList.add(new Contact("901", "John", "Doe", "858-888-8888"));
        testList.add(new Contact("902", "Jack", "Smith", "858-888-8888"));
        return testList;
    }

    private void init() {
        Contacts.getInstance().setEventListener(new Contacts.ContactEventListener() {
            @Override
            public void onContactListLoaded() {
                contacts = Contacts.getInstance().getContactList();
                if (adapter != null) {
                    adapter.notifyDataChange(contacts);
                }
            }

            @Override
            public void onNewContactAdded() {

            }

            @Override
            public void onContactUpdated(Contact oldContact, Contact newContact) {

            }
        });
    }
}
