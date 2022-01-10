package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer

class ServerJson {

    inner class StatusResponse {
        val description: String? = null
        val players: Players? = null
        val version: Version? = null
        val favicon: String? = null
        var time = 0
    }

    inner class Players {
        val max = 0
        val online = 0
        val sample: List<Player>? = null
    }

    inner class Player {
        val name: String? = null
        val id: String? = null
    }

    inner class Version {
        val name: String? = null
        val protocol: String? = null
    }
}