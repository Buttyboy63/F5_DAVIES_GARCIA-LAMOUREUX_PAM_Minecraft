package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.BDD.ServerDao
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Server
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ServersDatabase
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ViewModel.ServerDataViewModel
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.databinding.FragmentServerUpdatingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 * Use the [ServerUpdatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServerUpdatingFragment : Fragment() {
    private var _binding: FragmentServerUpdatingBinding? = null
    private val serverDataViewModel:ServerDataViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentServerUpdatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serverDataViewModel.updateServers()
        findNavController().popBackStack()
    }
}