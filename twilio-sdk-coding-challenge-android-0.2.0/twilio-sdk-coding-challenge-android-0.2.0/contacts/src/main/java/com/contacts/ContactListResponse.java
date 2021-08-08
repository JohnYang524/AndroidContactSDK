package com.contacts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
/*
    The API returns a list of Contacts
    we also need to create a class that will map the Contact key to a list of Contact objects.
*/
public class ContactListResponse {
    List<Contact> contactList;
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
