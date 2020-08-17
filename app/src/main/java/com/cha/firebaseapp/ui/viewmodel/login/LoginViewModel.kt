package com.cha.firebaseapp.ui.viewmodel.login

import android.app.Application
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.cha.firebaseapp.model.interfaces.LoginListener
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(application: Application): AndroidViewModel(application) {

    // Listener
    var loginListener: LoginListener ?= null

    // Read Data Input
    val userEmail    = ObservableField<String?>("")
    val userPassword = ObservableField<String?>("")

    fun validateUserInputs(view: View) {
        if ( !userEmail.get().isNullOrEmpty() && !userPassword.get().isNullOrEmpty() ) {
            loginListener?.showProgress()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail.get().toString(), userPassword.get().toString())
                .addOnCompleteListener {
                    if ( it.isSuccessful ) {
                        loginListener?.onSuccess()
                    } else {
                        loginListener?.onErrorLogin(1)
                    }
                    loginListener?.hideProgress()
                }

        } else {
            loginListener?.hideProgress()
            loginListener?.onErrorLogin(1)
        }
    }
}