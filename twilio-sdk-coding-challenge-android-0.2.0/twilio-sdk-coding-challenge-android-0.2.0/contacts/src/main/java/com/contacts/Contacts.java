package com.contacts;

import android.content.Context;

import com.contacts.models.Contact;

import java.util.List;

/**
 * The Contacts SDK allows users to manage a users contacts. Your submission should include the
 * following features:
 *
 * 1. List all of a users contacts.
 * 2. Add a new contact.
 * 3. Notify user of an updated contact.
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

    public String getContactListFromServer() {
        return nativeGetContactList();
    }

    public String getNewlyUpdatedContactListFromServer(String timestamp) {
        return nativeGetUpdatedContactListAfter(timestamp);
    }

    public String getLastUpdateTimeFromServer() {
        return nativeGetLastUpdateTime();
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
