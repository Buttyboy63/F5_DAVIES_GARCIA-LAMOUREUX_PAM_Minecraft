package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer

class ServerCell {
    enum class Status {ONLINE, UNKNOWN, OFFLINE}

    private val status = Status.UNKNOWN
    private val name = String
    private val current_players = Int
    private val max_players = Int
    private val version = String

    override fun toString(): String {
        return "$name $current_players/$max_players\t$version";
    }
}