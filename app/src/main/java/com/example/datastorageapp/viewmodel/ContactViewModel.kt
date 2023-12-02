package com.example.datastorageapp.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.datastorageapp.data.AppDatabase
import com.example.datastorageapp.data.Contact
import com.example.datastorageapp.data.ContactDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val contactDao: ContactDao = AppDatabase.getDatabase(application).contactDao()
    val allContacts: LiveData<List<Contact>> = contactDao.getAllContacts()


    fun importContactsFromDevice() = viewModelScope.launch(Dispatchers.IO) {
        // Get a ContentResolver instance to access the contacts
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver

        // Query the contacts on the device
        val contactsCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )

        val contactsList = mutableListOf<Contact>()

        contactsCursor?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            // Iterate through the contacts on the device
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)
                // Create a Contact object for each contact found
                val contact = Contact(
                    name = name,
                    phoneNumber = number,
                    email = null,
                    imageUri = null
                )
                contactsList.add(contact)
            }
        }

        // Insert all the contacts into the app's local database
        contactsList.forEach { contact ->
            val count = contactDao.countContactsByPhoneNumber(contact.phoneNumber)
            if (count == 0) {
            contactDao.insertContact(contact)
             }
        }
    }

    fun updateContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO)
    {
        contactDao.updateContact(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO)
    {
        contactDao.deleteContact(contact)
    }
}
