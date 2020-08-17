package com.cha.firebaseapp.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cha.firebaseapp.R
import com.cha.firebaseapp.adapter.UsersAdapter
import com.cha.firebaseapp.data.local.entity.AddUser
import com.cha.firebaseapp.data.local.models.Employee
import com.cha.firebaseapp.data.local.models.UserDataResponse
import com.cha.firebaseapp.databinding.ActivityHomeBinding
import com.cha.firebaseapp.model.interfaces.HomeListener
import com.cha.firebaseapp.ui.utils.hide
import com.cha.firebaseapp.ui.utils.show
import com.cha.firebaseapp.ui.utils.toast
import com.cha.firebaseapp.ui.viewmodel.home.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import java.io.File

class HomeActivity : AppCompatActivity(), HomeListener {



    // Databinding and View Model variables.
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView

    private lateinit var userList: ArrayList<Employee>
    private lateinit var list: AddUser
    private var message: String? = ""
    private var lastMessage = ""
    private var fileUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.homeviewmodel = viewModel
        viewModel.homeListener = this

        viewModel.getDataZip()

    }

    override fun showProgress() {
        home_progress_bar.show()
    }

    override fun hideProgress() {
        home_progress_bar.hide()
    }

    override fun onSuccess(urlFile: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            askPermissions()
        } else {
            downloadZip(urlFile)
        }
    }

    override fun onError(typeError: Number) {
        val errorConnection = getString(R.string.error_connection)
        if ( typeError == 1 ) {
            toast(errorConnection)
        }
    }

    override fun addUsers() {
        val addIntent = Intent(this, AddActivity::class.java)
        startActivity(addIntent)
    }


    override fun userClicked(user: AddUser, position: Int) {
        val intentMAp = Intent(this, DetailActivity::class.java)
        intentMAp.putExtra("name", user.name)
        intentMAp.putExtra("id", user.idUser)
        intentMAp.putExtra("latitude", user.latUser)
        intentMAp.putExtra("longitude", user.lngUser)
        intentMAp.putExtra("email", user.emailUser)
        startActivity(intentMAp)
    }


    @SuppressLint("NewApi")
    private fun downloadZip(url: String ) {
        val directory = File(Environment.DIRECTORY_DOWNLOADS)
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)

        if ( !directory.exists() ) {
            directory.mkdirs()
        }

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("employees_data.zip")
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    "employees_data.zip"
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()

                if ( cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL ) {
                    downloading = false
                }

                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                message = statusMessage(directory, status)

                if ( message != lastMessage ) {
                    this.runOnUiThread {
                        toast(message.toString())
                    }
                    lastMessage = message ?: ""
                }
                cursor.close()
            }
        }).start()

        readFile()
    }


    private fun readFile() {
        val fileIntent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(fileIntent, 10)
    }

    private fun statusMessage(directory: File, status: Int): String? {
        return when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL ->
                "File downloaded successfully in $directory" +
                        File.separator +
                        "employees_data.zip"
            else -> "There's nothing to download"
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun askPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save file from the Web.")
                    .setPositiveButton("Accept") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
                        finish()
                    }
                    .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)

            }
        } else {
            downloadZip(fileUrl)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadZip(fileUrl)
                } else {
                    toast("Failure")
                }
                return
            }
            else -> {
                toast("Failure")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val path = data?.data
        val jsonSelectedFile = path?.let { contentResolver.openInputStream(it) }
        val inputAsString = jsonSelectedFile?.bufferedReader().use {
            it?.readText()
        }

        val gson = Gson()
        val employee = gson.fromJson(inputAsString, UserDataResponse::class.java)
        val db = FirebaseFirestore.getInstance()
        val errorConnection = getString(R.string.network_failed)
        val saveDataToCloud = getString(R.string.save_data_to_db)

        recyclerView = findViewById(R.id.rv_items)
        userList = ArrayList()

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        //Add data

        userList = employee.data.employees as ArrayList<Employee>

        for ( index in userList ) {
            list = AddUser(
                index.name,
                index.id,
                index.location.lat,
                index.location.log,
                index.mail
            )
            viewModel.insert(list)
            db.collection("usuarios")
                .add(list)
                .addOnSuccessListener {
                    toast(saveDataToCloud)
                }
                .addOnFailureListener {
                    toast(errorConnection)
                }
        }

        viewModel.allItems.observe( this, Observer { items ->
            items?.let {
                val adapter = UsersAdapter(it, this)
                recyclerView.adapter = adapter
            }
        })

    }


    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}