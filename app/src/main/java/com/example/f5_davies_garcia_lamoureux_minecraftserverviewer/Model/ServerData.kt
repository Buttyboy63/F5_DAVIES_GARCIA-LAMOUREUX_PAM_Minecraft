package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model

import androidx.room.Entity

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
            return "$common_name $status $current_players/$max_players\t$version\t$ip:$port";
        }
    }