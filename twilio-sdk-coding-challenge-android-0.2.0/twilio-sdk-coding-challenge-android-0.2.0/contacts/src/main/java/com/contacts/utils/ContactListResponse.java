package com.contacts.utils;

import com.contacts.models.Contact;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
/**
 *  Parses JSON responses and returns a list of Contacts
 *  TODO: we also need to create a class that will map the Contact key to a list of Contact objects.
 */
public class ContactListResponse {
    public List<Contact> contactList;
    // public constructor is necessary for collections
    public ContactListResponse(List<Contact> contacts) {
        contactList = new ArrayList<Contact>();
        contactList.addAll(contacts);
    }

    public static ContactListResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        Contact[] contacts = gson.fromJson(response, Contact[].class);
        return new ContactListResponse(Arrays.asList(contacts));
    }
}
