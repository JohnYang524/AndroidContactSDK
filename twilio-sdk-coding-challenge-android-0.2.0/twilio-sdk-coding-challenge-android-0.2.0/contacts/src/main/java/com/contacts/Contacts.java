package com.contacts;

import android.content.Context;

import com.contacts.helper.ContactsManger;
import com.contacts.models.Contact;

import java.util.List;

/**
 * The Contacts SDK allows users to manage a user's contacts. The SDK includes the
 * following features:
 *
 * 1. List all of a user's contacts.
 * 2. Get list all of users contacts that were updated/created after a certain timestamp
 * 3. Add a new contact.
 * 4. Notify user of an updated contact.
 * 5. Get timestamp of last DB server update.
 *
 *  Singleton class for making Contacts API calls.
 */
public class Contacts {
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = Contacts.class.getName();

    private static Contacts contacts_instance = null;
    static {
        System.loadLibrary(BuildConfig.CONTACTS_LIBRARY);
    }

    private native void nativeAttachListener(ContactsManger.ContactEventListener listener);
    public native String nativeGetVersion();
    public native String nativeGetContactList();// Get a list all of user's contacts
    public native String nativeGetUpdatedContactListAfter(String timestamp);// Get a list all of users contacts that were updated/created after certain timestamp
    public native String nativeGetLastUpdateTime();
    public native void nativeAddNewContact(String contactData, ContactsManger.ContactEventListener listener);
    public native void nativeSimulateContactUpdateEvent();// Test event callback function when server data updated

    private ContactsManger manager;

    private Contacts() {
        manager = new ContactsManger();
    }

    public static Contacts getInstance()
    {
        if (contacts_instance == null)
            contacts_instance = new Contacts();

        return contacts_instance;
    }

    public List<Contact> getContactList() {
        return manager.getContactList();
    }

    public void syncContactData(Context context) {
        manager.syncContactList(context);
    }

    public void addNewContact(Contact contact, Context context) {
        manager.addNewContact(contact, context);
    }

    public void onServerDataUpdated(Context context) {
        manager.onServerDataUpdated(context); // Sync DB with server
    }

    public void setEventListener(ContactsManger.ContactEventListener listener) {
        manager.setEventListener(listener);
        // nativeInit after listener set
        nativeAttachListener(listener);
    }
}
