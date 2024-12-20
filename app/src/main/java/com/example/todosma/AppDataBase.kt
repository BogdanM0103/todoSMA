package com.example.todosma

import androidx.room.Database

@Database(entities = [User::class], version = 1)
abstract class AppDataBase {
    abstract fun userDao(): UserDao
}