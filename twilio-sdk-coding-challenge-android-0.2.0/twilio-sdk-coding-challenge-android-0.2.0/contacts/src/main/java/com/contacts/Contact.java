package com.contacts;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a user's contact.
 */
@Entity(tableName = "contact")
public class Contact {
    @NonNull
    @PrimaryKey
    String id;
    @ColumnInfo(name = "first_name")
    String firstName;
    @ColumnInfo(name = "last_name")
    String lastName;
    @ColumnInfo(name = "phone_number")
    String phoneNumber;

    public Contact(String id, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return formatName();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    private String formatName() {
        return firstName + " " + lastName;
    }
}
