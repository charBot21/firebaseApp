package com.cha.firebaseapp.data.network

import com.cha.firebaseapp.data.network.models.UsersFileZipResponse
import com.cha.firebaseapp.ui.utils.constants.END_POINT
import com.cha.firebaseapp.ui.utils.constants.environment
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DataZipApi {

    @GET(END_POINT.GET_USER_DATA_ZIP)
    suspend fun getDataZip(): Response<UsersFileZipResponse>

    companion object {
        operator fun invoke(): DataZipApi {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(environment.URL_BASE)
                .build()
                .create(DataZipApi::class.java)
        }
    }

}