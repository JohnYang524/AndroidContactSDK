package com.contacts;

import android.content.Context;

import com.contacts.database.ContactDatabase;
import com.contacts.models.Contact;
import com.contacts.models.ContactListResponse;
import com.contacts.utils.Util;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class ContactsTest {

    @Test
    public void db_testDAOfunctions() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ContactDatabase appDb = ContactDatabase.getInstance(getAppContext());
                Contact contact = appDb.contactDao().findById("100");
                if (contact != null) {
                    Assert.assertEquals(contact.id, "100");
                    appDb.contactDao().delete(contact);
                }
                Contact testContact = appDb.contactDao().findById("100");
                Assert.assertEquals(testContact, null);

                appDb.contactDao().insertAll(new Contact[]{contact});
                testContact = appDb.contactDao().findById("100");
                Assert.assertNotEquals(testContact, null);
            }
        });
    }

    @Test
    public void db_SequentialIDShouldBeAutoIncrementing() {
        String firstID = Util.getNextID(getAppContext());
        String secondID = Util.getNextID(getAppContext());
        Assert.assertTrue(Integer.parseInt(secondID) - Integer.parseInt(firstID) == 1);
    }

    @Test
    public void sdk_versionReturnsSemVerFormattedVersion() {
        String version = Contacts.getInstance().nativeGetVersion();
        assertNotNull(version);
    }

    @Test
    public void sdk_getServerLastUpdateTimeStamp_shouldReturnValidTimestamp() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String lastTimestamp = Contacts.getInstance().nativeGetLastUpdateTime();
                Assert.assertEquals(lastTimestamp, "1");
            }
        });
    }

    @Test
    public void sdk_getListOfContacts_shouleReturnServerData() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String jsonResponse = Contacts.getInstance().nativeGetContactList();
                List<Contact> updatedContactList = ContactListResponse.parseJSON(jsonResponse).contactList;
                Assert.assertNotEquals(updatedContactList, null);
                Assert.assertNotEquals(updatedContactList.size(), 0);
            }
        });
    }

    @Test
    public void sdk_testAddContact_shouldIncreaseListSizeByOne() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Contacts.getInstance().syncContactData(getAppContext());
                int previousListSize = Contacts.getInstance().getContactList().size();
                Contacts.getInstance().addNewContact(TestDataAndroid.testContact, getAppContext());
                int currentListSize = Contacts.getInstance().getContactList().size();

                Assert.assertEquals(previousListSize + 1, currentListSize);

                // Restore DB (remove added data)
                ContactDatabase appDb = ContactDatabase.getInstance(getAppContext());
                Contact toBeDeleted = appDb.contactDao().findById(TestDataAndroid.testContact.id);
                if (toBeDeleted != null)
                    appDb.contactDao().delete(toBeDeleted);
            }
        });
    }

    public Context getAppContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }
}