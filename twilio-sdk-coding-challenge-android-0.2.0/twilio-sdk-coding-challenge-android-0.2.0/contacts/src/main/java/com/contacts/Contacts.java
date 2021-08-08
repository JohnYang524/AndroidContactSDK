package com.contacts;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
    // static variable single_instance of type Contacts
    private static Contacts contacts_instance = null;
    static {
        System.loadLibrary(BuildConfig.CONTACTS_LIBRARY);
    }
    private native String nativeGetVersion();
    private native String nativeGetContactList();
    private native String nativeGetLastUpdateTime();

    // Event listeners to handle callbacks
    public interface ContactEventListener {
        void onContactListLoaded();
        void onNewContactAdded();
        void onContactUpdated(Contact oldContact, Contact newContact);
    }
    private ContactEventListener mEventListener;
    private List<Contact> mContactList;

    private static final String EVENTS_UPDATE_LIST = "EVENTS_UPDATE_LIST";

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

    public String getVersion() {
        return nativeGetVersion();
    }

    public String getContactListFromServer() {
        return nativeGetContactList();
    }

    public String getLastUpdateTimeFromServer() {
        return nativeGetLastUpdateTime();
    }

    public void setContactList(List<Contact> list) {
        mContactList.clear();
        mContactList.addAll(list);
        Log.v("ttt", "Server response length: " + mContactList.size());
    }

    public List<Contact> getContactList() {
        return mContactList;
    }


    // Check server update time before retrieving contact list and sync DB
    public void syncContactList(Context context) {
        //WorkRequest contactSyncRequest = new OneTimeWorkRequest.Builder(SyncContactsWorker.class).build();
        //WorkManager.getInstance(context).enqueue(contactSyncRequest);
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
        if (Util.localDBUpToDate(context, lastUpdateTimeInServer)) {// get contact list from Room DB
            Log.v("ttt", "DB up to date, getting data from local DB");
        } else { // DB not up-to-date, sync DB with server data
            Log.v("ttt", "DB NOT up to date, getting data from server");
            syncContactDataFromServer();
        }
    }

//    private void startContactSyncTask() {
////        WorkRequest contactSyncRequest = new OneTimeWorkRequest.Builder(SyncContactsWorker.class).build();
////        WorkManager.getInstance(context).enqueue(contactSyncRequest);
//        Executor executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                doInBackground();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        // do UI changes after background work here
//                        if (mEventListener != null) {
//                            mEventListener.onContactListLoaded();
//                        }
//                    }
//                });
//            }
//        });
//    }

    private void syncContactDataFromServer() {
        // Get list of Contacts by making call to C++ SDK
        String jsonString = getContactListFromServer();
        // Parse JSON string to a list of Contacts
        ContactListResponse listResponse = ContactListResponse.parseJSON(jsonString);
        setContactList(listResponse.contactList);
        // TODO: sync DB
    }

}
