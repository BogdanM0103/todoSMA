package com.example.todosma

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun syncRoomToFirebase(context: Context) {
    if (isInternetAvailable(context)) {
        CoroutineScope(Dispatchers.IO).launch {
            val roomUsers = MainActivity.dataBase.userDao().getAllUsers() // Get all Room data

            val firebaseDatabase = FirebaseDatabase.getInstance()
            val firebaseReference = firebaseDatabase.getReference("users")

            // Fetch Firebase data
            firebaseReference.get().addOnSuccessListener { snapshot ->
                val firebaseUsernames = mutableSetOf<String>()

                // Extract Firebase usernames
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        firebaseUsernames.add(user.username)
                    }
                }

                // Find Room users missing in Firebase
                val missingUsers = roomUsers.filter { it.username !in firebaseUsernames }

                // Add missing users to Firebase
                missingUsers.forEach { user ->
                    val userId = firebaseReference.push().key
                    if (userId != null) {
                        firebaseReference.child(userId).setValue(user)
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("FirebaseSync", "Error fetching data from Firebase", exception)
            }
        }
    } else {
        Log.d("SyncRoomToFirebase", "No internet connection. Sync skipped.")
    }
}