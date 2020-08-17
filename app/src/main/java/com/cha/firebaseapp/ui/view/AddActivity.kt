package com.cha.firebaseapp.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cha.firebaseapp.R
import com.cha.firebaseapp.data.local.entity.AddUser
import com.cha.firebaseapp.databinding.ActivityAddBinding
import com.cha.firebaseapp.model.interfaces.AddUserListener
import com.cha.firebaseapp.ui.utils.toast
import com.cha.firebaseapp.ui.viewmodel.add.AddViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AddActivity : AppCompatActivity(), AddUserListener {

    // Data binding and view model
    private lateinit var binding: ActivityAddBinding
    private lateinit var viewModel: AddViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add)
        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        binding.addviewmodel = viewModel
        viewModel.addListener = this
    }

    override fun onSuccess() {
        val name = findViewById<EditText>(R.id.et_username)
        val email = findViewById<EditText>(R.id.et_user_email)
        val id = findViewById<EditText>(R.id.et_user_id)

        val randomLat = (0..999999).random()
        val randomLng = (0..9999999).random()

        var lat = "19.$randomLat"
        var lng = "-99.$randomLng"

        val user = AddUser(
            name.text.toString(),
            id.text.toString(),
            lat,
            lng,
            email.text.toString()
        )
        viewModel.insert(user)
        Unit
        addData(user)

        finish()
    }

    override fun onError() {
        val errorMessage = getString(R.string.empty_fields_capture)

        toast(errorMessage)
    }

    private fun addData(user: AddUser) {
        val db = FirebaseFirestore.getInstance()
        val errorConnection = getString(R.string.network_failed)
        val saveDataToCloud = getString(R.string.save_data_to_db)


        db.collection("usuarios")
            .add(user)
            .addOnSuccessListener {
                toast(saveDataToCloud)
            }
            .addOnFailureListener {
                toast(errorConnection)
            }
    }
}