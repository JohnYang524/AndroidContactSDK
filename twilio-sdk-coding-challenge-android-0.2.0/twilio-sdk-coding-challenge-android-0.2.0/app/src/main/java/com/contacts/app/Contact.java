package com.contacts.app;

public class Contact {
    String name;
    String phoneNumber;

    public Contact(String name, String phone) {
        this.name = name;
        this.phoneNumber = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
