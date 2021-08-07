package com.contacts.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Lookup the recyclerview in activity layout
        RecyclerView contactList = (RecyclerView) findViewById(R.id.contactList);
        // Initialize contacts
        List<Contact> contacts = createTestList();
        // Create adapter passing in the sample user data
        ContactListAdapter adapter = new ContactListAdapter(contacts);
        // Attach the adapter to the recyclerview to populate items
        contactList.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactList.setLayoutManager(layoutManager);
        // Divider
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        contactList.addItemDecoration(mDividerItemDecoration);

        // add floating action button click
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void createNewContact() {
        Intent intent = new Intent(this, CreateContactAcitivity.class);
//        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private List<Contact> createTestList() {
        List<Contact> testList = new ArrayList<>();
        testList.add(new Contact("John", "Doe", "858-888-8888"));
        testList.add(new Contact("Jack", "Smith", "858-888-8888"));
        return testList;
    }
}
