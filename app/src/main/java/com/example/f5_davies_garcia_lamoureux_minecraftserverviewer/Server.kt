package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.minidns.hla.ResolverApi
import org.minidns.hla.SrvResolverResult
import java.io.*
import java.net.Inet4Address
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.nio.charset.Charset


class Server (
    _commonName: String,
    _hostName: String? = null,
    _ip: String? = null,
    _port: String? = null)

{
    private var commonName = _commonName
    private var hostName = _hostName
    private var ip = Inet4Address.getByName("127.0.0.1")// initialise ip
    private var ipStr: String = ""
    private var port = 25565
    private var success = false
    private var sock: Socket? = null
    private var inputStrm: InputStream? = null
    private var outputStrm: OutputStream? = null

    init {
        //port
        if (!_port.isNullOrBlank()) { //assign non default port
            port = _port.toInt()
        }
        if (!_ip.isNullOrBlank()) {
            ip = InetAddress.getByName(_ip)
        }

        // DNS resolution can override IP selection.
        if (!_hostName.isNullOrBlank()) {
            val result: SrvResolverResult = ResolverApi.INSTANCE.resolveSrv("_minecraft._tcp.$hostName")
            if (!result.wasSuccessful()) { // SRV failed -> basic DNS A request
                try {
                    ip = InetAddress.getByName(hostName) //DNS A request
                } catch (e : UnknownHostException) {
                    println("DNS resolution failed.")
                }
            } else {
                val srvRecords = result.sortedSrvResolvedAddresses
                // The order of the list depends on the preferred IP version setting of MiniDNS.
                port = srvRecords[0].port
                ip = srvRecords[0].addresses[0].inetAddress
            }
        }

        this.ipStr = ip.toString().substring(1)

        try {
            this.sock = Socket(this.ip, this.port)
            this.inputStrm = sock?.getInputStream()
            this.outputStrm = sock?.getOutputStream()
            success = true
        } catch (e: IOException) {
            println("Connection failed")
        }
    }

    //val : read only
    //var : mutable
    private fun writeVarInt(value: Int) : ByteArray {
    var tempVal: Int = value
    val b = ByteArrayOutputStream()
    val res = DataOutputStream(b)
    val hex7f = 0x7F
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
        var i = 0
        var j = 0
        var k: Int
        loop@ while (true) {
            k = dataIn.readByte().toInt()
            i = i.or(k.and(0x7F).shl(j++ * 7))
            //if j> 5 throw exception
            if (k.and(0x80) != 128)
                break@loop
        }
        return i
    }

    private fun closeSocket() {
        this.sock?.close()
    }

    //For debug purposes
    /*fun statusSocket(): String {
        val status: String = when {
            sock?.isBound == true -> "Bound"
            sock?.isClosed == true -> "Closed"
            else -> "Connected"
        }
        return "$status | ${sock?.localAddress}:${sock?.localPort} | ${sock?.inetAddress}:${sock?.port}"
    }*/

    private fun handshake(): ByteArray{
        val b = ByteArrayOutputStream()
        val handshake = DataOutputStream(b)
        val charset: Charset = Charsets.UTF_8
        val hostnameBa: ByteArray = ipStr.toByteArray(charset)

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

    private val json = Json { ignoreUnknownKeys = true }

    private fun getServerInfo(): ServerJson {

        val statusPaquet = statusPaquet()
        val dataOut = DataOutputStream(outputStrm)
        dataOut.write(statusPaquet)
        dataOut.flush()

        val dataIn = DataInputStream(inputStrm)
        readVarInt(dataIn) // Reads VarInt of the packet - Unused //uses up the stream bytes :)
        dataIn.readByte() // packet ID - Unused
        val bArray = ByteArray(readVarInt(dataIn))
        dataIn.read(bArray)
        closeSocket()
        println(bArray.decodeToString())
        return json.decodeFromString(bArray.decodeToString())
    }

    fun export(): ServerData {
        val srvData : ServerData = if (sock == null) {
            ServerData(ipStr, port, hostName, commonName, 0, null, null, null)
        } else {
            val srvJson = this.getServerInfo()
            ServerData(ipStr, port, hostName, commonName, 2 , srvJson.players.online, srvJson.players.max, srvJson.version.name)
        }
        return srvData
    }

    fun getSuccess(): Boolean {
        return success
    }
}

//For debug purposes
/*fun ByteArray.toHexString(): String {
    val hexChars = "0123456789abcdef".toCharArray()
    val hex = CharArray(2 * this.size)
    this.forEachIndexed { i, byte ->
        val unsigned = 0xff and byte.toInt()
        hex[2 * i] = hexChars[unsigned / 16]
        hex[2 * i + 1] = hexChars[unsigned % 16]
    }

    return hex.joinToString("")
}*/

fun String.toHex(): ByteArray { //Transform the HexString into a ByteArray
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}