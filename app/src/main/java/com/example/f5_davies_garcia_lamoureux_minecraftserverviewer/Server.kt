package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset

fun ByteArray.toHexString(): String {
    val hexChars = "0123456789abcdef".toCharArray()
    val hex = CharArray(2 * this.size)
    this.forEachIndexed { i, byte ->
        val unsigned = 0xff and byte.toInt()
        hex[2 * i] = hexChars[unsigned / 16]
        hex[2 * i + 1] = hexChars[unsigned % 16]
    }

    return hex.joinToString("")
}

fun String.toHex(): ByteArray { //Transform the HexString into a ByteArray
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

class Server (
    _commonName: String,
    _hostName: String? = null,
    _ip: String? = null,
    _port: Int = 25565)

{
    private var commonName = _commonName
    private var hostName = _hostName
    private var ip: InetAddress
    private var ip_str: String
    private var port = _port
    private lateinit var sock: Socket
    private val inputStrm: InputStream = sock.getInputStream()
    private val outputStrm: OutputStream = sock.getOutputStream()

    init {
        if (! _hostName.isNullOrBlank())
        {
            hostName = _hostName
            ip = InetAddress.getByName(hostName)
        }
        else
        {
            if (! _ip.isNullOrBlank())
                ip = InetAddress.getByName(_ip)
            else
                throw Exception("Ni IP ni hostname.")
        }

        this.sock = Socket(this.ip,this.port)
        this.ip_str = ip.toString().substring(1)

    }



    // TODO Add connected players on server detail/info. private val players = Player[]
    // TODO If masochist retriever the favicon of server. private val favicon = String  //wiki.vg/Server_List_ping

//val : read only
//var : mutable
    private fun writeVarInt(value: Int) : ByteArray {
    var tempVal: Int = value
    val b = ByteArrayOutputStream()
    val res = DataOutputStream(b)
    val hex7f: Int = 0x7F
    val hexInv7f: Int = hex7f.inv()
    while (true) {
        if (tempVal.and(hexInv7f) == 0x00) {
            res.write(tempVal)
            return b.toByteArray()
        }
        res.write(tempVal.and(hex7f).or(0x80))
        tempVal = tempVal.ushr(7)
    }
}
    private fun readVarInt(dataIn: DataInputStream): Int {
        var i: Int = 0
        var j: Int = 0
        var k: Int = 0
        loop@ while (true) {
            k = dataIn.readByte().toInt()
            i = i.or(k.and(0x7F).shl(j++ * 7))
            //if j> 5 throw exception
            if (k.and(0x80) != 128)
                break@loop
        }
        return i
    }

    fun closeSocket() { this.sock.close() }

    fun statusSocket(): String {
        val status: String
        if (sock.isBound)
            status = "Bound"
        else if (sock.isClosed)
            status = "Closed"
        else
            status = "Connected"
        return "$status | ${sock.localAddress}:${sock.localPort} | ${sock.inetAddress}:${sock.port}"
    }

    private fun handshake(): ByteArray{
        val b = ByteArrayOutputStream()
        val handshake = DataOutputStream(b)
        val charset: Charset = Charsets.UTF_8
        val hostnameBa: ByteArray = ip_str.toByteArray(charset)

        handshake.writeByte(0x00) //handshake packet ID
        handshake.write("FFFFFFFF0F".toHex()) //-1 var int as per convention for ping
        handshake.writeByte(hostnameBa.size) //Length of hostname
        handshake.write(hostnameBa) //Same IP or hostname as used to connect the socket
        handshake.writeShort(port)
        handshake.writeByte(0x01) //Status (1) and not login (2)

        handshake.close()
        return b.toByteArray()
    }

    private fun statusPaquet(): ByteArray {
        val paquet = ByteArrayOutputStream()
        val handshake = handshake()
        paquet.write(writeVarInt(handshake.size))
        paquet.write(handshake)

        //ping
        paquet.write(0x01)
        paquet.write(0x00)
        return paquet.toByteArray()
    }

    fun getServerInfo(): ServerJson {
        val statusPaquet = statusPaquet()
        val dataOut: DataOutputStream = DataOutputStream(outputStrm)
        dataOut.write(statusPaquet)
        dataOut.flush()

        val dataIn: DataInputStream = DataInputStream(inputStrm)
        readVarInt(dataIn) // Reads VarInt of the packet - Unused //uses up the stream bytes :)
        dataIn.readByte() // packet ID - Unused
        val bArray = ByteArray(readVarInt(dataIn))
        dataIn.read(bArray)
        return Json.decodeFromString(bArray.decodeToString())
    }
}