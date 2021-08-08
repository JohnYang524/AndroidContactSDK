package com.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.math.BigInteger;

public class Util {
    public static String PREF_KEY_LAST_SYNC_TIME = "PREF_KEY_LAST_SYNC_TIME";
    public static String getLastSyncTime(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_KEY_LAST_SYNC_TIME, "0");
    }

    // Save room DB last sync time in millisecond
    public static void saveLastSyncTime(Context context, String newValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref != null) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREF_KEY_LAST_SYNC_TIME, newValue);
            editor.apply();
        }
    }

    /***
     * Method to check to see if local Room db date is up-to-date
     * by comparing dbLastSync timestamp with the timestamp saved in server
     * dbLastSyncTime > serverLastUpdateTime -> DB is up-to-date
     *
     * @param lastUpdateInServer timestamp for last sync time in local DB
     * @return whether or not we need to update Room DB
     *
     * */
    public static boolean localDBUpToDate(Context context, String lastUpdateInServer) {
        String lastDBSyncTime = getLastSyncTime(context);
        return new BigInteger(lastDBSyncTime).compareTo(new BigInteger(lastUpdateInServer)) > 0;
    }
}
