package com.contacts;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.contacts.utils.ContactListResponse;
import com.contacts.database.ContactDatabase;
import com.contacts.models.Contact;
import com.contacts.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = Contacts.class.getName();
    // static variable single_instance of type Contacts
    private static Contacts contacts_instance = null;
    static {
        System.loadLibrary(BuildConfig.CONTACTS_LIBRARY);
    }
    private native String nativeGetVersion();
    private native String nativeGetContactList();
    private native String nativeGetUpdatedContactListAfter(String timestamp);
    private native String nativeGetLastUpdateTime();

    // Event listeners to handle callbacks
    public interface ContactEventListener {
        void onContactListLoaded();
        void onNewContactAdded();
        void onContactUpdated(Contact oldContact, Contact newContact);
    }
    private ContactEventListener mEventListener;
    private List<Contact> mContactList;

//    private static final String EVENTS_UPDATE_LIST = "EVENTS_UPDATE_LIST";

    private Contacts() {
        mContactList = new ArrayList<>();
    }

    // static method to create instance of Contacts class
    public static Contacts getInstance()
    {
        if (contacts_instance == null)
            contacts_instance = new Contacts();

        return contacts_instance;
    }

    public void setEventListener(ContactEventListener listener) {
        this.mEventListener = listener;
    }

    public List<Contact> getContactList() {
        return mContactList;
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

     /**
      * Update local contact list data.
      * TODO: use a better logic to update local list. E.g. update list after server call before updating DB
      * For now, update local list with Room DB data
     */
    public void updateLocalContactList(Context context) {

        ContactDatabase appDb = ContactDatabase.getInstance(context);
        List<Contact> dbList = appDb.contactDao().getAll();
        mContactList.clear();
        mContactList.addAll(dbList);

        /*if (list.isEmpty()) {
            mContactList.addAll(list);
        }
        else { // Make changes to the newly updated contact record
            for (Contact updatedContact : list) {
                if (mContactList.contains(updatedContact)) { // Update existing record
                    mContactList.remove(updatedContact);
                }
                mContactList.add(updatedContact);
            }
        }*/
        if (mIsDebuggable)
            Log.v(TAG, "Updated local contact list. Current length: " + mContactList.size());
    }

    // Check server update time before retrieving contact list and sync DB
    public void syncContactList(Context context) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                syncContactListInBackground(context);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // do UI changes after background work
                        if (mEventListener != null) {
                            mEventListener.onContactListLoaded();
                        }
                    }
                });
            }
        });
    }

    // Step 1: Retrieve last update time in server
    // Step 2: Based on last sync timestamp, the list is updated either from Room DB,
    // or from Web service call responses from C++ SDK
    // Step 3: If list is updated with server data, update local DB
    public void syncContactListInBackground(Context context) {
        String lastUpdateTimeInServer = getLastUpdateTimeFromServer();
        if (Util.localDBUpToDate(context, lastUpdateTimeInServer)) {// DB up-to-date, get contact list from Room DB
            if (mIsDebuggable)
                Log.v(TAG, "Local DB up to date, getting data from local DB");
            updateLocalContactList(context);
        } else { // DB not up-to-date, sync DB with server data
            if (mIsDebuggable)
                Log.v(TAG, "Local DB NOT up to date, getting data from server");
            syncContactDataFromServer(context);
        }
    }

    // TODO: update UI before syncing DB to improve performance
    private void syncContactDataFromServer(Context context) {
        // Get timestamp for last Room DB update
        String lastDBUpdateTimestamp = Util.getLastSyncTime(context);
        // Get list of NEW/Updated Contacts by making call to C++ SDK
        String jsonResponse = getNewlyUpdatedContactListFromServer(lastDBUpdateTimestamp);
        // Parse JSON string to a list of Contacts, and update local list and DB
        List<Contact> updatedContactList = ContactListResponse.parseJSON(jsonResponse).contactList;
//        updateLocalContactList(updatedContactList);
        // Update Room DB to match server data
        syncLocalDB(context, updatedContactList);
        // Update DB update timestamp
        // TODO: change "0" to be current timestamp, hardcoding the value here for testing only
        Util.saveLastSyncTime(context, "0");
        // Update local list
        updateLocalContactList(context);
    }

    // Update Room DB to match server data
    // TODO: use sync adapters to improve db data modification performance
    private void syncLocalDB(Context context, List<Contact> contactList) {
        ContactDatabase appDb = ContactDatabase.getInstance(context);
//        Contact[] contactsArray = new Contact[mContactList.size()];
//        appDb.contactDao().insertAll(contactsArray);
        for (Contact contact : contactList) {
            Contact dbRecord = appDb.contactDao().findById(contact.id);
            if (dbRecord != null) {
                // Delete existing record
                appDb.contactDao().delete(dbRecord);
            }
            // insert new record
            appDb.contactDao().insertAll(new Contact[]{contact});
        }
    }
}
