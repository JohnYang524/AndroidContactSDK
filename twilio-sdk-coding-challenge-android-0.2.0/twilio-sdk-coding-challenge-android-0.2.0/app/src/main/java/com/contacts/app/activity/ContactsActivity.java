package com.contacts.app.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.contacts.Contacts;

import com.contacts.app.helpers.ContactActivityObserver;
import com.contacts.app.R;
import com.contacts.app.databinding.ActivityContactsBinding;

/**
 * This application to demonstrate the capabilities of the Contacts SDK you have implemented.
 * Specifically this app should contain the following features:
 *
 * 1. List all of a user's contacts.
 * 2. Add a contact
 * 3. Update the UI when a contact is updated.
 */

public class ContactsActivity extends AppCompatActivity {
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = ContactsActivity.class.getName();

    private AppBarConfiguration appBarConfiguration;
    private ActivityContactsBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getLifecycle().addObserver(new ContactActivityObserver());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Button to simulate a server update
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_simulate:
                // Simulate contact-updated event and test callbacks
                Contacts.getInstance().nativeSimulateContactUpdateEvent();
                return true;
            case R.id.action_restore:
                // Restore contact data to original dataset
                Contacts.getInstance().restoreContactData(this);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
