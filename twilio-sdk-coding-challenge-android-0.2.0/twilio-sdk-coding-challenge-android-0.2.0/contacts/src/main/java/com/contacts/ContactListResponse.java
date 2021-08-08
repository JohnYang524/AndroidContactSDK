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
    public ContactListResponse() {
        contactList = new ArrayList<Contact>();
    }

    // TODO: move this out of this class and created a Util class to do this job
    public static ContactListResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        ContactListResponse contactListResponse = gson.fromJson(response, ContactListResponse.class);
        return contactListResponse;
    }
}
