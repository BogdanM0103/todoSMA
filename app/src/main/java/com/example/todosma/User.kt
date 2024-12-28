package com.example.todosma

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String
) {
    // No-argument constructor required by Firebase
    constructor() : this(0, "")
}
