package com.cha.firebaseapp.data.network.repositories

import android.util.Log
import com.cha.firebaseapp.data.network.DataZipApi
import com.cha.firebaseapp.data.network.models.UsersFileZipResponse
import retrofit2.Response

class DataZipRepository {

    suspend fun getDataZIp(): Response<UsersFileZipResponse> {
        return DataZipApi().getDataZip()
    }
}