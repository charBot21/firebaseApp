package com.cha.firebaseapp.ui.viewmodel.home

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cha.firebaseapp.data.local.AddUserRoomDatabase
import com.cha.firebaseapp.data.local.entity.AddUser
import com.cha.firebaseapp.data.local.models.Employee
import com.cha.firebaseapp.data.local.repository.AddUserLocalRepository
import com.cha.firebaseapp.data.network.repositories.DataZipRepository
import com.cha.firebaseapp.model.interfaces.HomeListener
import com.cha.firebaseapp.ui.utils.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application) {

    var homeListener: HomeListener ?= null

    // Database
    private val repository: AddUserLocalRepository
    val allItems: LiveData<List<AddUser>>

    init {
        val addUsersDao = AddUserRoomDatabase.getDatabase(application).addUserDao()
        repository = AddUserLocalRepository(addUsersDao)
        allItems = repository.allUsers
    }

    fun getDataZip() {
        homeListener?.showProgress()

        Coroutines.main {
            val response = DataZipRepository().getDataZIp()

            if ( response.isSuccessful ) {
                homeListener?.onSuccess(response.body()?.data?.file!!)
            } else {
                homeListener?.onError(1)
            }
            homeListener?.hideProgress()
        }
    }

    fun openAddUsers(view: View) {
        homeListener?.addUsers()
    }

    fun insert(addUser: AddUser) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUser(addUser)
    }
}