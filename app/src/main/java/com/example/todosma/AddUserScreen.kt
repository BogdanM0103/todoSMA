package com.example.todosma

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
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
    var userList by remember { mutableStateOf<List<String>>(emptyList()) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Handle the submit action here
                val newUser = User(
                    username = username
                )
                //Add the newUser here
                // Launching a coroutine to add user to database
                CoroutineScope(Dispatchers.IO).launch {
                    MainActivity.dataBase.userDao().insertUser(newUser)
                }

            }) {
                Text(text = "Submit!")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val users = MainActivity.dataBase.userDao().getAllUsers()
                        withContext(Dispatchers.Main) {
                            userList = users.map { it.username }
                        }
                    }
                }
            ) {
                Text(text = "Show Usernames")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        userList.forEach { user ->
            TextField(
                value = user,
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }
    }
}
