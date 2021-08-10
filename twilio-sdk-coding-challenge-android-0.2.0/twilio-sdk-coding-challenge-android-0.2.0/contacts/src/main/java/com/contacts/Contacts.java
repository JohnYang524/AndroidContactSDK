package com.contacts;

import android.content.Context;

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
    private native String nativeGetVersion();
    private native String nativeGetContactList();
    private native String nativeGetUpdatedContactListAfter(String timestamp);
    private native String nativeGetLastUpdateTime();
    private native void nativeAddNewContact(String contactData, ContactsManger.ContactEventListener listener);

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

    public String getVersion() {
        return nativeGetVersion();
    }

    // Get a list all of user's contacts
    public String getContactListFromServer() {
        return nativeGetContactList();
    }

    // Get a list all of users contacts that were updated/created after timestamp
    public String getNewlyUpdatedContactListFromServer(String timestamp) {
        return nativeGetUpdatedContactListAfter(timestamp);
    }

    public String getLastUpdateTimeFromServer() {
        return nativeGetLastUpdateTime();
    }

    public void sendNewContactDataToServer(String contactData, ContactsManger.ContactEventListener listener) {
        nativeAddNewContact(contactData, listener);
    }
    public void addNewContact(Contact contact, Context context) {
        manager.addNewContact(contact, context);
    }

    public void onServerDataUpdated(Context context) {
        manager.onServerDataUpdated(context); // Sync DB with server
    }

    public void setEventListener(ContactsManger.ContactEventListener listener) {
        manager.setEventListener(listener);
    }

    public List<Contact> getContactList() {
        return manager.getContactList();
    }

    public void syncContactData(Context context) {
        manager.syncContactList(context);
    }
}
