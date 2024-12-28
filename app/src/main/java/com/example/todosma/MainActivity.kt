package com.example.todosma

import android.net.Network
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.todosma.ui.theme.TodoSMATheme
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var dataBase: AppDatabase
        lateinit var database: DatabaseReference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Creating the local database Room
        dataBase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my_database",
        ).build()


        // Creating the remote database Firebase
        database = Firebase.database.reference

        // Register network callback to sync Room with Firebase when the internet is restored
        registerNetworkCallback(this)

        enableEdgeToEdge()
        setContent {
            TodoSMATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddUserScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TodoSMATheme {
        Greeting("Android")
    }
}