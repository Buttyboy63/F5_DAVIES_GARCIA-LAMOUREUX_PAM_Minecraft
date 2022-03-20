package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments.Recycler

import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData

class ServerCell ( serverData: ServerData) {
    private var status = serverData.status
    private var name = serverData.common_name
    private var current_players = serverData.current_players
    private var max_players = serverData.max_players
    private var version = serverData.version

    override fun toString(): String {
        return "$name $current_players/$max_players\t$version";
    }
}