package com.contacts

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactsTest {
    @Test
    fun versionReturnsSemVerFormattedVersion() {
        val semVerRegex = Regex("^([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:-([0-9A-Z" +
                "a-z-]+(?:\\.[0-9A-Za-z-]+)*))?(?:\\+[0-9A-Za-z-]+)?$")
        val version = Contacts.version
        assertNotNull(version)
        assertTrue(version.matches(semVerRegex))
    }
}