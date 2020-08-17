package com.cha.firebaseapp.data.local.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.cha.firebaseapp.data.local.dao.AddUserDao
import com.cha.firebaseapp.data.local.entity.AddUser

class AddUserLocalRepository( private val addUsersDao: AddUserDao) {

    val allUsers: LiveData<List<AddUser>> = addUsersDao.getUsers()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertUser(addUser: AddUser) {
        addUsersDao.insertUser(addUser)
    }
}