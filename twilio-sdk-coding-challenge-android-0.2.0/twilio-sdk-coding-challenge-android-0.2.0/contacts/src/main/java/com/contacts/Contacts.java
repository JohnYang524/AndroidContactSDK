package com.contacts;

import java.util.List;

/**
 * The Contacts SDK allows users to manage a users contacts. Your submission should include the
 * following features:
 *
 * 1. List all of a users contacts.
 * 2. Add a new contact.
 * 3. Notify user of an updated contact.
 *
 * The class includes a simple [.getVersion] method to demonstrate how to call from a
 * Java class to C++ code.
 */
public class Contacts {
    // static variable single_instance of type Singleton
    private static Contacts contacts_instance = null;

    static {
        System.loadLibrary(BuildConfig.CONTACTS_LIBRARY);
    }

    private native String nativeGetVersion();
    private native String nativeGetContactList();

    private Contacts() {}

    // static method to create instance of Contacts class
    public static Contacts getInstance()
    {
        if (contacts_instance == null)
            contacts_instance = new Contacts();

        return contacts_instance;
    }

    public String getVersion() {
        return nativeGetVersion();
    }

    public String getContactList() { // TODO: change to List<Contact>
        return nativeGetContactList();
    }



}
