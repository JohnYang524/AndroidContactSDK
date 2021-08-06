package com.contacts

import org.junit.Assert
import org.junit.Test

class ContactsUnitTest {
    @Test
    fun nativeLibraryNameShouldBeDefined() {
        Assert.assertNotNull(BuildConfig.CONTACTS_LIBRARY)
    }
}