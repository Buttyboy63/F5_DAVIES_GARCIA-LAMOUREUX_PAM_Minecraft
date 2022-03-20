package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ViewModel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.BDD.ServerDao
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.R
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Server
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ServersDatabase
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ToastHelper
import kotlinx.coroutines.*
import java.lang.Exception

class ServerDataViewModel(application: Application) : AndroidViewModel(application) {
    private val app = getApplication<Application>()
    private val database: ServerDao = ServersDatabase.getInstance(getApplication<Application>().applicationContext).serverDao()

    fun addNewServer(srvName: String, hostname: String, ip: String, port: String){
        lateinit var server: Server
        viewModelScope.launch(Dispatchers.IO) {
            server = Server(srvName, hostname, ip, port)
            insertServer(server.export())

            if(!server.getSuccess()) {
                println("Test: connexion impossible")
                runBlocking(Dispatchers.Main) {
                    //ViewModel n'as pas accès aux ressources locales, dommage....
                    Toast.makeText(app, "Connexion impossible!", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                runBlocking(Dispatchers.Main) {
                    //ToastHelper.printToastShort(app, R.string.toast_connection_impossible)
                    Toast.makeText(app, "Serveur ajouté!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun insertServer(srvdata: ServerData){
        println("function insert server")
        //Insertion BDD
        try {
            database.insertOne(srvdata)
        }
        catch (e: SQLiteConstraintException) {
            println("ret: Server existe déja")
            runBlocking(Dispatchers.Main) {
                Toast.makeText(app, "Le serveur éxiste déja!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateServers() {
        viewModelScope.launch(Dispatchers.IO) {
            val dataSet = ArrayList(database.getAll())
            for (s in dataSet) {
                launch {
                    val serv = Server(s.common_name, s.hostname, s.ip, s.port.toString())
                    database.updateServer(serv.export())
                }
            }
        }
    }

    fun deleteServer(server : ServerData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                database.delete(server)
            } catch (e: Exception) {
                runBlocking(Dispatchers.Main) {
                    Toast.makeText(app, "Erreur suppression", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getServerByIP(ip: String, port: Int) : ServerData {
        return database.findByIp(ip,port)
    }

    fun getServerByHostname(hostname: String) : List<ServerData> {
        return database.findByHostname(hostname)
    }
}