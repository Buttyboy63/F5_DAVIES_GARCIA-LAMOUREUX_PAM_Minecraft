package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ViewModel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.BDD.ServerDao
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Server
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.BDD.ServersDatabase
import kotlinx.coroutines.*
import kotlin.Exception

class ServerDataViewModel(application: Application) : AndroidViewModel(application) {
    private val app = getApplication<Application>()
    private val database: ServerDao = ServersDatabase.getInstance(getApplication<Application>().applicationContext).serverDao()

    fun addNewServer(srvName: String, hostname: String, ip: String, port: String){
        lateinit var server: Server
        viewModelScope.launch(Dispatchers.IO) {
            server = Server(srvName, hostname, ip, port)
            try {
                insertServer(server.export())
                if(!server.getSuccess()) {
                    println("Test: connexion impossible")
                    runBlocking(Dispatchers.Main) {
                        //ViewModel n'as pas accès aux ressources locales, dommage....
                        Toast.makeText(app, "Could not connect", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    runBlocking(Dispatchers.Main) {
                        Toast.makeText(app, "Server added!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception){
                runBlocking(Dispatchers.Main) {
                    Toast.makeText(app, "Unknown error", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun insertServer(srvdata: ServerData){
        println("function insert server")
        //Insertion BDD
        try {
            database.insertOne(srvdata)
        }
        catch (e: SQLiteConstraintException) {
            println("ret: Server existe déja")
            runBlocking(Dispatchers.Main) {
                Toast.makeText(app, "Server already exists!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
    //Tentative récupération arrayList co routine
    fun getAllServers() : ArrayList<ServerData> {
        var array : ArrayList<ServerData> = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            array = ArrayList(database.getAll())
        }
        return array
    }*/
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
                    Toast.makeText(app, "Deletion error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}