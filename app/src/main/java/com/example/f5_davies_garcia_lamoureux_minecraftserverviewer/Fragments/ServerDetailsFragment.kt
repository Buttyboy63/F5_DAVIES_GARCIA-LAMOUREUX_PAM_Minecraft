package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.R
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ViewModel.ServerDataViewModel
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.databinding.FragmentServerDetailsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ServerDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServerDetailsFragment : Fragment() {
    private var _binding: FragmentServerDetailsBinding? = null
    private val serverDataViewModel: ServerDataViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val server: ServerData = arguments?.get("server") as ServerData
        binding.serverName.text = server.common_name

        val statusText : String = when(server.status) {
            0 -> "patate"
            2 -> "Online"
            else -> {
                "Unknown"
            }
        }
        binding.statusVal.text = statusText

        if (server.status == 0) {
            binding.playersVal.text = String.format("%d / %d", server.current_players,server.max_players)
            binding.versionVal.text = server.version
        }

        if (!server.hostname.isNullOrEmpty()) {
            binding.hostnameVal.text = server.hostname
        }
        else {
            binding.hostnameVal.text = "Aucun hostname défini."
        }

        if (server.ip.isNotEmpty()) {
            binding.ipPortVal.text = String.format("%s : %d",server.ip, server.port)
        }
        else {
            binding.ipPortVal.text = "Inconnus."
        }

        binding.deleteButton.setOnClickListener {
            //Appel asynchrone de suppression
            serverDataViewModel.deleteServer(server)
            //Dépile pour retour au fragment
            findNavController().popBackStack()
        }

    }
}