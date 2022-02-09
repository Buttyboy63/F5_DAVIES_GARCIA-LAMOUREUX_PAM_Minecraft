package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.minidns.dnsmessage.DnsMessage.RESPONSE_CODE
import org.minidns.hla.ResolverApi
import org.minidns.hla.SrvResolverResult
import org.minidns.hla.SrvResolverResult.ResolvedSrvRecord
import org.minidns.record.InternetAddressRR
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import kotlinx.coroutines.*
import java.net.Inet4Address
import kotlin.coroutines.CoroutineContext


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

//@RequiresApi(Build.VERSION_CODES.Q)
class Server (
    _commonName: String,
    _hostName: String? = null,
    _ip: String? = null,
    _port: String? = null)

{
    private var commonName = _commonName
    private var hostName = _hostName
    private var ip = Inet4Address.getLocalHost()// initialise ip
    private var ipStr: String
    private var port = 25565
    private var sock: Socket? = null
    private var inputStrm: InputStream? = null
    private var outputStrm: OutputStream? = null

    init {
        //port
        if (! _port.isNullOrBlank()) { //assign non default port
            port = _port.toInt()
        }

        if (! _hostName.isNullOrBlank())
        {
            hostName = _hostName
            val result: SrvResolverResult = ResolverApi.INSTANCE.resolveSrv("_minecraft._tcp.$hostName")
            if (! result.wasSuccessful()) { // SRV failed -> basic DNS A request
                ip = InetAddress.getByName(hostName) //DNS A request
            }
            else {
                val srvRecords = result.sortedSrvResolvedAddresses
                for (srvRecord in srvRecords) {
                    // Loop over the Internet Address RRs resolved for the SRV RR. The order of
                    // the list depends on the preferred IP version setting of MiniDNS.
                    port = srvRecord.port
                    for (inetAddressRR in srvRecord.addresses) {
                        ip = inetAddressRR.inetAddress
                    }
                }
            }
        }
        else
        {
            if (! _ip.isNullOrBlank())
                ip = InetAddress.getByName(_ip)
            else
                throw Exception("Ni IP ni hostname.")
        }
        this.ipStr = ip.toString().substring(1)

        try {
            this.sock = Socket(this.ip,this.port)
            this.inputStrm = sock?.getInputStream()
            this.outputStrm = sock?.getOutputStream()

        } catch (e: IOException ) {
            println("Connection failed")
        }
    }

    // TODO Add connected players on server detail/info. private val players = Player[]
    // TODO If masochist retrieve the favicon of server. private val favicon = String  //wiki.vg/Server_List_ping

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

    private fun resolveSRV(domainName: String): Int { //Returns 1 if success, 0 otherwise
        var status: Int = 0;
        //result = DnssecResolverApi.INSTANCE.resolveSrv(SrvType.xmpp_client, "example.org");
        val result: SrvResolverResult = ResolverApi.INSTANCE.resolveSrv("_minecraft._tcp.$domainName");

        if (result.wasSuccessful()) {
            val srvRecords = result.sortedSrvResolvedAddresses
            if (srvRecords.size > 0) {
                val srvRecord = srvRecords[0]
                val inetAddressRR = srvRecord.addresses[0]

                this.port = srvRecord.port
                this.ip = inetAddressRR.inetAddress
                status = 1
            }
        }
        else {
            val responseCode = result.responseCode
            // Perform error handling.
        }
        return status
    }

    fun closeSocket() {
        this.sock?.close()
    }

    fun statusSocket(): String {
        val status: String
        if (sock?.isBound == true)
            status = "Bound"
        else if (sock?.isClosed == true)
            status = "Closed"
        else
            status = "Connected"
        return "$status | ${sock?.localAddress}:${sock?.localPort} | ${sock?.inetAddress}:${sock?.port}"
    }

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
        println(bArray.decodeToString())
        return json.decodeFromString(bArray.decodeToString())
    }

    fun export(): ServerData {
        val srvData : ServerData
        if (sock == null) {
            srvData = ServerData(ipStr, port, hostName, commonName, 0, null, null, null)
        }
        else {
            val srvJson = this.getServerInfo()
            // TODO : Returns an "UNKNOWN" status (2) for now. Needs to differentiate cases like "online", "offline", and adapts parameters.
            srvData = ServerData(ipStr, port, hostName, commonName, 2 , srvJson.players.online, srvJson.players.max, srvJson.version.name)
        }
        return srvData;
    }
}