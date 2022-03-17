package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Server
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ServerDao
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ServersDatabase
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.databinding.FragmentServerUpdatingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 * Use the [ServerUpdatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServerUpdatingFragment : Fragment() {
    private var _binding: FragmentServerUpdatingBinding? = null

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
        val serversDAO: ServerDao = ServersDatabase.getInstance(requireContext()).serverDao()
        runBlocking(Dispatchers.Default) {
            val dataSet = ArrayList(serversDAO.getAll())
            for (s in dataSet) {

                val serv = Server(s.common_name, s.hostname, s.ip, s.port.toString())
                serversDAO.updateServer(serv.export())
            }
        }
        findNavController().popBackStack()
    }
}