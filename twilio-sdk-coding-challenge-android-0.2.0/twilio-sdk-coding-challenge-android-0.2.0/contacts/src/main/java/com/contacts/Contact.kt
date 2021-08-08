package com.contacts

/**
 * Represents a user's contact.
 */
data class Contact(val firstName: String, val lastName: String, val phoneNumber: String){
    fun getName(): String {
        return firstName.plus(" ").plus(lastName)
    }
}


