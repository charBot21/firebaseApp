package com.cha.firebaseapp.model.interfaces

import com.cha.firebaseapp.data.local.entity.AddUser

interface HomeListener {

    fun showProgress()
    fun hideProgress()
    fun onSuccess( urlFile: String )
    fun onError(typeError: Number)
    fun addUsers()
    fun userClicked(user: AddUser, position: Int)
}