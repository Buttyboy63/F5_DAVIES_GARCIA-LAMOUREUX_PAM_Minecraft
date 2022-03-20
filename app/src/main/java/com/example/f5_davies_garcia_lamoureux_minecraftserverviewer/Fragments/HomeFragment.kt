package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.R
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.BDD.ServerDao
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments.Recycler.ServerDataAdapter
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Model.ServerData
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.BDD.ServersDatabase
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ViewModel.ServerDataViewModel
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.databinding.FragmentFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var dataSet = ArrayList<ServerData>()
    private val serverDataViewModel: ServerDataViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val serversDAO : ServerDao = ServersDatabase.getInstance(requireContext()).serverDao()
        runBlocking(Dispatchers.Default) { dataSet = ArrayList(serversDAO.getAll()) }
        //dataSet = runBlocking(Dispatchers.IO) {serverDataViewModel.getAllServers()}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(view.context)
        val adapter = ServerDataAdapter(dataSet, findNavController())
        recyclerview.adapter = adapter


        binding.buttonUpdate.setOnClickListener {
            //todo re run LSP requests
            serverDataViewModel.updateServers()
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_addServerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}