package com.contacts.app.helpers;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class ContactActivityObserver implements LifecycleObserver {
    private static final boolean mIsDebuggable = true; // TODO: use value in config file
    private String TAG = this.getClass().getSimpleName();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreateEvent() {
        if (mIsDebuggable) Log.v(TAG, "onCreate Event");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResumeEvent() {
        if (mIsDebuggable) Log.v(TAG, "OnResume Event");
    }
}
