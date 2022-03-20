package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.R
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ToastHelper
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.ViewModel.ServerDataViewModel
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.databinding.FragmentAddServerBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddServerFragment : Fragment() {

    private var _binding: FragmentAddServerBinding? = null
    private val serverDataViewModel:ServerDataViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddServerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSecond.setOnClickListener {
            serverDataViewModel.addNewServer(
                binding.textInputServerNameField.text.toString(),
                binding.textInputHostnameField.text.toString(),
                binding.textInputIpv4Field.text.toString(),
                binding.textInputPortField.text.toString()
            )


            findNavController().popBackStack()
        }

        // todo inputtypes on fragment xml
        // todo better graphics
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}