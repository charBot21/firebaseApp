package com.cha.firebaseapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cha.firebaseapp.data.local.entity.AddUser

@Dao
interface AddUserDao {
    @Query("SELECT * from users_table")
    fun getUsers(): LiveData<List<AddUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(addUser: AddUser)
}