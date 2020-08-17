package com.cha.firebaseapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class AddUser (
    @PrimaryKey()
    @ColumnInfo(name = "user_name")
    val name: String,
    @ColumnInfo(name = "user_id")
    val idUser: String,
    @ColumnInfo(name = "user_location")
    val latUser: String,
    val lngUser: String,
    @ColumnInfo(name = "user_email")
    val emailUser: String
)