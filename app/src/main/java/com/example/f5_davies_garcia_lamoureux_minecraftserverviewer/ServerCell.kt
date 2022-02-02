package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer

class ServerCell ( serverData: ServerData ) {
    /*
    enum class Status {OFFLINE, ONLINE, UNKNOWN}

    private var status = Status.UNKNOWN
    private var name = _name
    private var current_players = 0
    private var max_players = 0
    private var version = "0.0.0"

    init {
        when (_status) {
            0 -> this.status = Status.OFFLINE
            1 -> this.status = Status.ONLINE
        }
        if(serverJson != null)
        {
            status = Status.ONLINE
            current_players = serverJson.players.online
            max_players = serverJson.players.max
            version = serverJson.version.name
        }
    }

     */

    private var status = serverData.status
    private var name = serverData.common_name
    private var current_players = serverData.current_players
    private var max_players = serverData.max_players
    private var version = serverData.version

    override fun toString(): String {
        return "$name $current_players/$max_players\t$version";
    }
}