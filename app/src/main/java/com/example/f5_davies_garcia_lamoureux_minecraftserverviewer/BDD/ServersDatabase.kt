package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer

import android.content.Context
import androidx.room.*

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

@Entity(tableName = "servers_table", primaryKeys = ["ip","port"])
data class ServerData(
    val ip: String,
    val port: Int,
    val hostname: String?,
   val common_name: String,
    val status: Int,
    val current_players: Int?,
    val max_players: Int?,
    val version: String?
)
{
    override fun toString(): String {
        return "$common_name $current_players/$max_players\t$version\t$ip:$port";
    }
}

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers_table")
    fun getAll(): List<ServerData>

    @Query("SELECT * FROM servers_table WHERE ip LIKE :ip AND " +
            "port = :port LIMIT 1")
    fun findByIp(ip: String, port: Int): ServerData

    @Query("SELECT * FROM servers_table WHERE hostname LIKE :hostname")
    fun findByHostname(hostname: String): List<ServerData>

    @Query("SELECT * FROM servers_table WHERE common_name LIKE :commonName LIMIT 1")
    fun findByName(commonName: String): ServerData

    @Insert
    fun insertAll(vararg servers: ServerData)

    @Insert
    fun insertOne(server: ServerData)

    @Update
    fun updateServer(server: ServerData)

    @Delete
    fun delete(server: ServerData)
}