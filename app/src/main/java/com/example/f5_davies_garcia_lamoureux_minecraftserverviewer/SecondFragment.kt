package com.example.f5_davies_garcia_lamoureux_minecraftserverviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.f5_davies_garcia_lamoureux_minecraftserverviewer.databinding.FragmentSecondBinding

fun disableButton(button: Button) {
    button.isEnabled = false
    button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
    button.setBackgroundColor(ContextCompat.getColor(button.context, R.color.grey))
}

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val serversDAO : ServerDao = ServersDatabase.getInstance(requireContext()).serverDao()

        binding.buttonSecond.setOnClickListener {
            disableButton(binding.buttonSecond)
            val serv = Server(binding.textInputServerNameField.text.toString(),
                binding.textInputHostnameField.text.toString(),
                binding.textInputIpv4Field.text.toString(),
                binding.textInputPortField.text.toString())
            serversDAO.insertAll(serv.export())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}