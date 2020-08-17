package com.cha.firebaseapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cha.firebaseapp.R
import com.cha.firebaseapp.data.PreferencesProvider
import com.cha.firebaseapp.databinding.ActivityLoginBinding
import com.cha.firebaseapp.model.interfaces.LoginListener
import com.cha.firebaseapp.ui.utils.hide
import com.cha.firebaseapp.ui.utils.show
import com.cha.firebaseapp.ui.utils.toast
import com.cha.firebaseapp.ui.viewmodel.login.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginListener {

    private lateinit var binding        : ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel
    private lateinit var preferences    : PreferencesProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Databinding
        binding                      = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel               = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginviewmodel       = loginViewModel
        loginViewModel.loginListener = this

        preferences                  = PreferencesProvider(applicationContext)
        validateActiveSession()
    }

    private fun validateActiveSession() {
        if ( preferences.getBoolean("success") ) {
            val successIntent = Intent(this, HomeActivity::class.java)
            startActivity(successIntent)
        }
    }

    override fun showProgress() {
        progress_bar.show()
    }

    override fun hideProgress() {
        progress_bar.hide()
    }

    override fun onSuccess() {
        val successIntent = Intent(this, HomeActivity::class.java)

        preferences.putBoolean("success", true)

        //
        startActivity(successIntent)
    }

    override fun onErrorLogin(typeError: Number) {
        val emptyMessage : String = getString(R.string.empty_fields_login)
        val invalidData  : String = getString(R.string.invalid_user)
        preferences.putBoolean("success", false)

        if ( typeError == 1 ) {
            toast(emptyMessage)
        } else if ( typeError == 2 ) {
            toast(invalidData)
        }
    }
}