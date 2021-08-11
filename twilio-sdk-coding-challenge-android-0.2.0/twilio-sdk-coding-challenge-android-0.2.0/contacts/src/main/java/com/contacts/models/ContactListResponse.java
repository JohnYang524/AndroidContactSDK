package com.contacts.models;

import com.contacts.utils.Util;

import java.util.*;
/**
 *  Parses JSON responses and returns a list of Contacts
 *  TODO: For fast data manipulation, create a map that maps the Contact key to a list of Contact objects.
 */
public class ContactListResponse {
    public List<Contact> contactList;

    public ContactListResponse(List<Contact> contacts) {
        contactList = new ArrayList<>();
        contactList.addAll(contacts);
    }

    public static ContactListResponse parseJSON(String response) {
        Contact[] contacts = Util.jsonStringToContact(response);
        return new ContactListResponse(Arrays.asList(contacts));
    }
}
