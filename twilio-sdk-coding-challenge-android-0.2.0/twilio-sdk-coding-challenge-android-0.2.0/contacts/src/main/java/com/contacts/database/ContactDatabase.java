package com.contacts.database;

import android.content.Context;

import com.contacts.models.Contact;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
*  Room database for saving contacts data.
 *  Singleton class
*/
@Database(entities = {Contact.class}, version = 1)
public abstract class ContactDatabase extends RoomDatabase {
    private static final String DB_NAME = "contact_db";
    private static ContactDatabase instance;

    public static synchronized ContactDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ContactDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return instance;
    }

    public abstract ContactDAO contactDao();
}
