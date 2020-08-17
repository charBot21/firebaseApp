package com.cha.firebaseapp.model.interfaces

interface LoginListener {

    fun showProgress()
    fun hideProgress()
    fun onSuccess()
    fun onErrorLogin(typeError: Number)
}