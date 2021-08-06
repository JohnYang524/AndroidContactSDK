package com.contacts

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
class Contacts {
    companion object {
        init {
            System.loadLibrary(BuildConfig.CONTACTS_LIBRARY)
        }

        /**
         * Returns a semantic versioning formatted string.
         */
        @JvmStatic
        val version: String
            get() = nativeGetVersion()

        private external fun nativeGetVersion(): String
    }
}