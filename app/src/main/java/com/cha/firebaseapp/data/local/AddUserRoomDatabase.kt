package com.cha.firebaseapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cha.firebaseapp.data.local.dao.AddUserDao
import com.cha.firebaseapp.data.local.entity.AddUser

@Database(entities = [AddUser::class], version = 1)
abstract class AddUserRoomDatabase: RoomDatabase() {

    abstract fun addUserDao(): AddUserDao

    companion object {
        @Volatile
        private var INSTANCE: AddUserRoomDatabase ?= null

        fun getDatabase(context: Context): AddUserRoomDatabase {
            val tmpInstance = INSTANCE

            if ( tmpInstance != null ) {
                return tmpInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AddUserRoomDatabase::class.java,
                    "users_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}