package com.contacts.app.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.contacts.ContactsManger;

import com.contacts.app.ContactActivityObserver;
import com.contacts.app.R;
import com.contacts.app.databinding.ActivityContactsBinding;

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

    private AppBarConfiguration appBarConfiguration;
    private ActivityContactsBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

}
