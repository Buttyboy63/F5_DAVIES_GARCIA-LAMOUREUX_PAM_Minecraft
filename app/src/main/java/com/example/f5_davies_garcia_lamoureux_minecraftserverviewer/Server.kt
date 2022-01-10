package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer

import com.google.gson.Gson
import java.io.*
import java.lang.RuntimeException
import java.net.InetSocketAddress
import java.net.Socket

class Server {
    private val name = String
    private val address = String
    private val port = "25565"
    private val host : InetSocketAddress? = null
    private var timeout = 7000

    private val status = String
    private val version = String
    private val maxPlayers = Int
    private val onlinePlayers = Int
    private val motd = String
    // TODO Add connected players on server detail/info. private val players = Player[]
    // TODO If masochist retriever the favicon of server. private val favicon = String  //wiki.vg/Server_List_ping

    private val gson: Gson = Gson()


    @Throws(IOException::class)
    fun readVarInt(input: DataInputStream): Int {
        var i = 0; var j = 0
        while (true) {
            val k: Int = input.readByte()
            i = i or (k and 0x7F shl j++ * 7)
            if (j > 5) throw RuntimeException("VarInt too big")
            if (k and 0x80 != 128) break
        }
        return i
    }

    @Throws(IOException::class)
    fun writeVarInt(out: DataOutputStream, paramInt: Int) {
        var paramInt = paramInt
        while (true) {
            if (paramInt and -0x80 == 0) {
                out.writeByte(paramInt)
                return
            }
            out.writeByte(paramInt and 0x7F or 0x80)
            paramInt = paramInt ushr 7
        }
    }

    @Throws(IOException::class)
    fun fetchData(): StatusResponse {
        val socket = Socket()
        val outputStream: OutputStream
        val dataOutputStream: DataOutputStream
        val inputStream: InputStream
        val inputStreamReader: InputStreamReader
        socket.setSoTimeout(timeout)
        socket.connect(host, timeout)
        outputStream = socket.getOutputStream()
        dataOutputStream = DataOutputStream(outputStream)
        inputStream = socket.getInputStream()
        inputStreamReader = InputStreamReader(inputStream)
        val b = ByteArrayOutputStream()
        val handshake = DataOutputStream(b)
        handshake.writeByte(0x00) //packet id for handshake
        writeVarInt(handshake, 4) //protocol version
        writeVarInt(handshake, host.getHostString().length()) //host length
        handshake.writeBytes(host.getHostString()) //host string
        handshake.writeShort(host.getPort()) //port
        writeVarInt(handshake, 1) //state (1 for handshake)
        writeVarInt(dataOutputStream, b.size()) //prepend size
        dataOutputStream.write(b.toByteArray()) //write handshake packet
        dataOutputStream.writeByte(0x01) //size is only 1
        dataOutputStream.writeByte(0x00) //packet id for ping
        val dataInputStream = DataInputStream(inputStream)
        val size = readVarInt(dataInputStream) //size of packet
        var id = readVarInt(dataInputStream) //packet id
        if (id == -1) {
            throw IOException("Premature end of stream.")
        }
        if (id != 0x00) { //we want a status response
            throw IOException("Invalid packetID")
        }
        val length = readVarInt(dataInputStream) //length of json string
        if (length == -1) {
            throw IOException("Premature end of stream.")
        }
        if (length == 0) {
            throw IOException("Invalid string length.")
        }
        val input = ByteArray(length)
        dataInputStream.readFully(input) //read json string
        val json = String(input)
        val now = System.currentTimeMillis()
        dataOutputStream.writeByte(0x09) //size of packet
        dataOutputStream.writeByte(0x01) //0x01 for ping
        dataOutputStream.writeLong(now) //time!?
        readVarInt(dataInputStream)
        id = readVarInt(dataInputStream)
        if (id == -1) {
            throw IOException("Premature end of stream.")
        }
        if (id != 0x01) {
            throw IOException("Invalid packetID")
        }
        val pingtime: Long = dataInputStream.readLong() //read response
        val response: StatusResponse = gson.fromJson(json, ServerJson::class)
        response.time = (now - pingtime).toInt()
        dataOutputStream.close()
        outputStream.close()
        inputStreamReader.close()
        inputStream.close()
        socket.close()
        return response
    }
}