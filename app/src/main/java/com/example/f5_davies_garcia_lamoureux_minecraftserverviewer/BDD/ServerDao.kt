package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.BDD

import androidx.room.*
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData

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