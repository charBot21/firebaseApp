package com.cha.firebaseapp.ui.viewmodel.add

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cha.firebaseapp.data.local.AddUserRoomDatabase
import com.cha.firebaseapp.data.local.entity.AddUser
import com.cha.firebaseapp.data.local.repository.AddUserLocalRepository
import com.cha.firebaseapp.model.interfaces.AddUserListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddViewModel(application: Application): AndroidViewModel(application) {

    // Listener
    var addListener: AddUserListener ?= null

    // Inputs
    val userName  = ObservableField<String?>("")
    val userEmail = ObservableField<String?>("")
    val userID    = ObservableField<String?>("")

    // Database
    private val repository: AddUserLocalRepository
    val allItems: LiveData<List<AddUser>>

    init {
        val addUsersDao = AddUserRoomDatabase.getDatabase(application).addUserDao()
        repository = AddUserLocalRepository(addUsersDao)
        allItems = repository.allUsers
    }

    fun addUsers(view: View) {
        if ( !userName.get().isNullOrEmpty() && !userEmail.get().isNullOrEmpty() && !userID.get().isNullOrEmpty() ) {
            addListener?.onSuccess()
        } else {
            addListener?.onError()
        }
    }

    fun insert(addUser: AddUser) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUser(addUser)
    }
}