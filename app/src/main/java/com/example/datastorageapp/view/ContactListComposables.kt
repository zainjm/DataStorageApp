import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.datastorageapp.data.Contact
import com.example.datastorageapp.makePhoneCall
import com.example.datastorageapp.sendMessage
import com.example.datastorageapp.viewmodel.ContactViewModel

@Composable
fun ContactListScreen(contactViewModel: ContactViewModel) {
    val contacts by contactViewModel.allContacts.observeAsState(initial = emptyList())
    val context = LocalContext.current

    ContactList(contacts = contacts, context = context, contactViewModel = contactViewModel)
}

@Composable
fun ContactList(contacts: List<Contact>, context: Context, contactViewModel: ContactViewModel) {
    // State variables for the UI

    Column(modifier = Modifier.fillMaxSize()) {
        // Button to import contacts from the device
        Button(
            onClick = { contactViewModel.importContactsFromDevice()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Import Contacts")
        }
        LazyColumn {
            items(contacts) { contact ->
                ContactRow(contact = contact, context = context, contactViewModel = contactViewModel)
            }
        }
    }
}

@Composable
fun ContactRow(contact: Contact, context: Context, contactViewModel: ContactViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* Handle contact click */ }
    ) {
        var isEditDialogOpen by remember { mutableStateOf(false) }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = contact.name, style = MaterialTheme.typography.h6)
            Text(text = contact.phoneNumber)
        }
        IconButton(onClick = { isEditDialogOpen = true }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { contactViewModel.deleteContact(contact) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
        IconButton(onClick = { makePhoneCall(contact.phoneNumber, context) }) {
            Icon(Icons.Default.Phone, contentDescription = "Call")
        }
        IconButton(onClick = { sendMessage(contact.phoneNumber, context) }) {
            Icon(Icons.Default.Send, contentDescription = "Message")
        }

        if (isEditDialogOpen) {
            EditContactDialog(
                contact = contact,
                onDismiss = { isEditDialogOpen = false },
                onSave = { updatedContact ->
                    contactViewModel.updateContact(updatedContact)
                    isEditDialogOpen = false
                }
            )
        }
    }

}

@Composable
fun EditContactDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onSave: (Contact) -> Unit
) {
    var newName by rememberSaveable { mutableStateOf(contact.name) }
    var newPhoneNumber by rememberSaveable { mutableStateOf(contact.phoneNumber) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Edit Contact") },
        text = {
            Column {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") }
                )
                TextField(
                    value = newPhoneNumber,
                    onValueChange = { newPhoneNumber = it },
                    label = { Text("Phone Number") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(contact.copy(name = newName, phoneNumber = newPhoneNumber))
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
