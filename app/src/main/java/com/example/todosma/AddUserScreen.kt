package com.example.todosma

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.todosma.MainActivity.Companion.dataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddUserScreen(
    modifier: Modifier
) {
    // State for the username field
    var username by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(emptyList<String>()) } // Ensure it's a List<String>
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(50.dp)
        ) {
            // Input for username
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button to add user
            Button(onClick = {
                val newUser = User(username = username)
                CoroutineScope(Dispatchers.IO).launch {
                    MainActivity.dataBase.userDao().insertUser(newUser)
                }
            }) {
                Text(text = "Submit!")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            // Fetch users button
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val users = MainActivity.dataBase.userDao().getAllUsers()
                        withContext(Dispatchers.Main) {
                            userList = users.map { it.username } // Ensure this returns a List<String>
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Show Usernames")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delete database button
            Button(
                onClick = {
                    context.deleteDatabase("my_database") // Use LocalContext to access the database
                    userList = emptyList() // Clear UI
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Delete Database")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Displaying usernames in a LazyColumn as plain Text
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(userList) { user ->
                    Text(
                        text = user,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}