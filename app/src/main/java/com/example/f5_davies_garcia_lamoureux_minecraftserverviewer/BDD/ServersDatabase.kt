package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.BDD

import android.content.Context
import androidx.room.*
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData

@Database(entities = [ServerData::class], version = 1)
abstract class ServersDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    companion object {
        @Volatile
        private var INSTANCE: ServersDatabase? = null

        fun getInstance(context: Context): ServersDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ServersDatabase::class.java,
                        "servers_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}