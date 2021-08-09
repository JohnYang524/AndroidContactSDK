package com.contacts;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.contacts.database.ContactDatabase;
import com.contacts.models.Contact;
import com.contacts.utils.ContactListResponse;
import com.contacts.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Class for managing contact data update flow and keeping business logic.
 */
public class ContactsManger {
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private static final String TAG = ContactsManger.class.getName();

    // Event listeners to handle callbacks
    public interface ContactEventListener {
        void onContactListLoaded();
        void onNewContactAdded(int responseCode);
        void onContactUpdated(Contact oldContact, Contact newContact);
    }
    private ContactEventListener mEventListener;
    private List<Contact> mContactList;

    public ContactsManger() {
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
        String lastUpdateTimeInServer = Contacts.getInstance().getLastUpdateTimeFromServer();
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
     */
    public void syncContactDataFromServer(Context context) {
        // Get timestamp for last Room DB update
        String lastDBUpdateTimestamp = Util.getLastSyncTime(context);
        // Get list of NEW/Updated Contacts by making call to C++ SDK
        String jsonResponse = Contacts.getInstance().getNewlyUpdatedContactListFromServer(lastDBUpdateTimestamp);
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
            Contact dbRecord = appDb.contactDao().findById(contact.id);
            if (dbRecord != null) {
                // Delete existing record
                appDb.contactDao().delete(dbRecord);
            }
            // insert new record
            appDb.contactDao().insertAll(new Contact[]{contact});
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
     * Update local DB
     * @param contact
     */
    public void onNewContactAdded(Contact contact, Context context) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ContactDatabase appDb = ContactDatabase.getInstance(context);
                appDb.contactDao().insertAll(new Contact[]{contact});
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

    /**
     * Sync DB data with server when server update is detected
     * @param context
     */
    public void onServerDataUpdated(Context context){
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                syncContactDataFromServer(context);
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
}
