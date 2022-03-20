package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.R
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ViewModel.ServerDataViewModel
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.databinding.FragmentServerDetailsBinding

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
            0 -> getString(R.string.offline)
            2 -> getString(R.string.online)
            else -> {
                getString(R.string.unknown)
            }
        }
        binding.statusVal.text = statusText

        if (server.status == 2) {
            binding.playersVal.text = String.format("%d / %d", server.current_players,server.max_players)
            binding.versionVal.text = server.version
        }

        if (!server.hostname.isNullOrEmpty()) {
            binding.hostnameVal.text = server.hostname
        }
        else {
            binding.hostnameVal.text = getString(R.string.details_undefined_hostname)
        }

        if (server.ip.isNotEmpty()) {
            binding.ipPortVal.text = String.format("%s : %d",server.ip, server.port)
        }
        else {
            binding.ipPortVal.text = getString(R.string.details_unknown_ip_port)
        }

        binding.deleteButton.setOnClickListener {
            //Appel asynchrone de suppression
            serverDataViewModel.deleteServer(server)
            //Dépile pour retour au fragment
            findNavController().popBackStack()
        }

    }
}