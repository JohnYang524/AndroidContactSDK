package com.contacts;

import com.contacts.models.Contact;

public class TestData {
    public static final Contact testContact = new Contact("1", "TestFirst", "TestLast", "1111111111");
    public static final String testJSONString = "[{\"id\" : \"1\",\"firstName\" : \"TestFirst\",\"lastName\" : \"TestLast\",\"phoneNumber\" : \"+1111111111\"}]";
}
