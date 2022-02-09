package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer
import kotlinx.serialization.Serializable

@Serializable
data class ServerJson (
    val version: Version,
    var players: Players,
    var description: Description,
    var favicon: String? = null
)

@Serializable
data class Version (
    var name: String,
    var protocol: Int
)

@Serializable
data class Players (
    var max: Int = 0,
    var online: Int = 0,
    var sample: List<Player>? = null
)

@Serializable
data class Player (
    var name: String,
    var id: String
)

@Serializable
data class Description (
    var text: String
)