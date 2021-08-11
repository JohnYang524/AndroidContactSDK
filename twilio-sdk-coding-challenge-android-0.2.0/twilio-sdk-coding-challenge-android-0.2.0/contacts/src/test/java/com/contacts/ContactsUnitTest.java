package com.contacts;

import com.contacts.models.Contact;
import com.contacts.utils.Util;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContactsUnitTest {

    @Test
    public void nativeLibraryNameShouldBeDefined() {
        Assert.assertNotNull(BuildConfig.CONTACTS_LIBRARY);
    }

    @Test
    public void jsonToContact() {
        Contact[] newContacts = Util.jsonStringToContact(TestData.testJSONString);
        Assert.assertNotNull(newContacts);
        Assert.assertNotEquals(newContacts.length, 0);
        Assert.assertEquals(newContacts[0].id, "1");
    }

    @Test
    public void contactToJSON() {
        String jsonString = Util.contactToJSONString(TestData.testContact);
        Assert.assertNotNull(jsonString);
        Assert.assertNotEquals(jsonString.length(), 0);
        Assert.assertTrue(jsonString.contains("phoneNumber"));
        Assert.assertTrue(jsonString.contains("TestLast"));
    }

}
