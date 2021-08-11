package com.contacts.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.contacts.Contacts;
import com.contacts.database.ContactDatabase;
import com.contacts.models.Contact;
import com.contacts.models.ContactListResponse;
import com.contacts.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Class for managing contact data update flow and keeping business logic.
 */
public class ContactsManager {
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = ContactsManager.class.getName();

    // Event listeners to handle callbacks
    public interface ContactEventListener {
        void onContactListLoaded();
        void onNewContactAdded(int responseCode);
        void onContactUpdated(String newContactData);
    }

    private ContactEventListener mEventListener;
    private List<Contact> mContactList;
    private Executor executor;

    public ContactsManager() {
        mContactList = new ArrayList<>();
    }

    public void setEventListener(ContactEventListener listener) {
        mEventListener = listener;
    }

    public ContactEventListener getEventListener() {
        return mEventListener;
    }

    public List<Contact> getContactList() {
        return mContactList;
    }

    /**
     * Method for updating contactList.
     * Based on Room DB last sync timestamp, the local contactlist is updated either from Room DB,
     *     or from Web service call responses.
     * @param context Context
     */
    public void syncContactList(Context context) {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                syncContactListInBackground(context);
                notifyUI();
            }
        });
    }

    /**
     * Tasks running in background to sync data between server, local DB and contact list.
     * Step 1: Retrieve last update time in server
     * Step 2: Based on last sync timestamp, the list is updated either from Room DB,
     *          or from Web service call responses from C++ SDK
     * Step 3: If list is updated with server data, update local DB and update lastUpdateTimestamp
     * Step 4: Update local contactList
     *
     * @param context Context
     */
    public void syncContactListInBackground(Context context) {
        String lastUpdateTimeInServer = Contacts.getInstance().nativeGetLastUpdateTime();
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

    /**
     * Makes calls to C++ SDK to get last update timestamp,
     * based on which we decide whether we should update contactlist with local DB or server data.
     * TODO: update UI before syncing DB to improve performance
     * Step 1: Retrieve last update time in server
     * Step 2: Based on last sync timestamp, the list is updated either from Room DB,
     *           or from Web service call responses from C++ SDK
     * Step 3: If list is updated with server data, update local DB and update lastUpdateTimestamp
     * Step 4: Update local contactList
     */
    public void syncContactDataFromServer(Context context) {
        // Get timestamp for last Room DB update
        String lastDBUpdateTimestamp = Util.getLastSyncTime(context);
        // Get list of NEW/Updated Contacts by making call to C++ SDK
        String jsonResponse = Contacts.getInstance().nativeGetUpdatedContactListAfter(lastDBUpdateTimestamp);
        // Parse JSON string to a list of Contacts, and update local list and DB
        List<Contact> updatedContactList = ContactListResponse.parseJSON(jsonResponse).contactList;
//        updateLocalContactList(updatedContactList);
        // Update Room DB to match server data
        syncLocalDBWithServer(context, updatedContactList);
        // Update DB update timestamp
        // TODO: change "0" to be current timestamp, hardcoding the value here for testing only
        Util.saveLastSyncTime(context, "0");
        // Update local list
        updateLocalContactList(context);
    }

    /**
     * Update Room DB to match server data.
     * Get records that were newly updated from server, and modify them in local DB
     * TODO: use sync adapters to improve db data modification performance
     */
    private void syncLocalDBWithServer(Context context, List<Contact> contactList) {
        ContactDatabase appDb = ContactDatabase.getInstance(context);
        for (Contact contact : contactList) {
            updateContactDataInDB(appDb, contact);
        }
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
        Collections.sort(mContactList, (a, b) -> (Integer.parseInt(a.id) - Integer.parseInt(b.id)));

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

    /**
     * Add a new contact record.
     * 1. Send contact json string to C++ SDK
     * 2. Send a listener to receive callback
     * 3. Update local DB (for testing purpose since server doesn't exist
     * @param contact Contact
     * @param context
     */
    public void addNewContact(Contact contact, Context context) {
        String jsonString = Util.contactToJSONString(contact);
        if (mIsDebuggable)
            Log.v(TAG, "New contact data created: " + jsonString);

        // Update local DB
        onNewContactAdded(contact, context);
        // Update server
        Contacts.getInstance().nativeAddNewContact(jsonString, mEventListener);
    }

    /**
     * Update local DB when new contact is added. For testing purpose.
     * @param contact
     */
    private void onNewContactAdded(Contact contact, Context context) {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ContactDatabase appDb = ContactDatabase.getInstance(context);
                appDb.contactDao().insertAll(new Contact[]{contact});// insert new contact
            }
        });
    }

    /**
     * Sync DB data with server when server update is detected
     *
     * @param context
     */
    public void onServerDataUpdated(Context context){
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                syncContactDataFromServer(context);
                notifyUI();
            }
        });
    }

    /**
     * Called when contact data is updated in server.
     * Update local DB and local list.
     *
     * @param context
     */
    public void onContactDataUpdate(String newContactData, Context context){
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Contact[] contacts = Util.jsonStringToContact(newContactData);
                ContactDatabase appDb = ContactDatabase.getInstance(context);
                if (contacts != null && contacts.length > 0)
                    updateContactDataInDB(appDb, contacts[0]);
                updateLocalContactList(context);
                notifyUI();
            }
        });
    }

    /**
     * Do not call in UI thread.
     * Update contact data in Room DB.
     *
     * @param appDb Database
     * @param contact New data
     */
    private void updateContactDataInDB(ContactDatabase appDb, Contact contact) {
        Contact dbRecord = appDb.contactDao().findById(contact.id);
        if (dbRecord != null) {
            // Delete existing record
            appDb.contactDao().delete(dbRecord);
        }
        // insert new record with updated data
        appDb.contactDao().insertAll(new Contact[]{contact});
    }

    /**
     * Restore dataset to use the original data list
     * For testing only
     *
     * @param context
     */
    public void restoreData(Context context) {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Clear DB
                ContactDatabase appDb = ContactDatabase.getInstance(context);
                appDb.contactDao().nukeContactTable();
                // Reload server origin data
                syncContactDataFromServer(context);
                notifyUI();
            }
        });
    }

    /**
     * Calling from non-UI thread to refresh UI
     */
    private void notifyUI() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // do UI changes after background work
                if (mEventListener != null) {
                    mEventListener.onContactListLoaded();
                }
            }
        });
    }
}
