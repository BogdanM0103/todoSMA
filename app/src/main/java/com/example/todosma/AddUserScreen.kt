package com.example.todosma

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.todosma.MainActivity.Companion.database
import com.google.firebase.database.FirebaseDatabase
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
                val userId = MainActivity.database.push().key
                val isOnline = isInternetAvailable(context)

                CoroutineScope(Dispatchers.IO).launch {
                    // Always store in Room
                    MainActivity.dataBase.userDao().insertUser(newUser)

                    if (isOnline) {
                        // Store in Firebase if the device is online
                        if (userId != null) {
                            MainActivity.database.child("users").child(userId).setValue(newUser)
                        }
                    } else {
                        // Show a message to the user if offline
                        withContext(Dispatchers.Main) {
                            // Replace this with a Toast or Snackbar for better UX
                            println("No internet connection. Data saved in Room only.")
                        }
                    }
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
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        // Fetch data from Room first
                        val roomUsers = MainActivity.dataBase.userDao().getAllUsers().map { it.username }

                        if (isInternetAvailable(context)) {
                            // Fetch data from Firebase and merge it with Room data
                            MainActivity.database.child("users").get().addOnSuccessListener { snapshot ->
                                val firebaseUsers = mutableListOf<String>()
                                snapshot.children.forEach {
                                    val user = it.getValue(User::class.java)
                                    if (user != null) {
                                        firebaseUsers.add(user.username)
                                    }
                                }

                                // Merge Room and Firebase data, remove duplicates, and update the UI
                                CoroutineScope(Dispatchers.Main).launch {
                                    userList = (roomUsers + firebaseUsers).distinct()
                                }
                            }.addOnFailureListener {
                                // Handle Firebase fetch failure
                                CoroutineScope(Dispatchers.Main).launch {
                                    userList = roomUsers // Fallback to Room data only
                                }
                            }
                        } else {
                            // No internet, show Room data only
                            withContext(Dispatchers.Main) {
                                userList = roomUsers
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Show Usernames")
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        // Step 1: Clear Room database
                        MainActivity.dataBase.clearAllTables()

                        // Step 2: Clear Firebase database
                        MainActivity.database.child("users").removeValue()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("DeleteDatabase", "Firebase database cleared successfully.")
                                } else {
                                    Log.e("DeleteDatabase", "Failed to clear Firebase database.", task.exception)
                                }
                            }

                        // Step 3: Clear UI data
                        withContext(Dispatchers.Main) {
                            userList = emptyList()
                        }
                    }
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