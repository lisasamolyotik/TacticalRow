package com.tactical.row.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tactical.row.R
import com.tactical.row.databinding.FragmentSettingsBinding
import com.tactical.row.ui.MainActivity

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        if (!MainActivity.isVibroON) {
            binding.vibroSwitch.setImageResource(R.drawable.off_button)
            binding.vibroValueText.setText(R.string.off)
        }
        if (!MainActivity.isSoundON) {
            binding.soundSwitch.setImageResource(R.drawable.off_button)
            binding.soundValueText.setText(R.string.off)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.soundSwitch.setOnClickListener {
            if (MainActivity.isSoundON) {
                MainActivity.isSoundON = false
                binding.soundSwitch.setImageResource(R.drawable.off_button)
                binding.soundValueText.setText(R.string.off)
            } else {
                MainActivity.isSoundON = true
                binding.soundSwitch.setImageResource(R.drawable.on_button)
                binding.soundValueText.setText(R.string.on)
            }
        }

        binding.vibroSwitch.setOnClickListener {
            if (MainActivity.isVibroON) {
                MainActivity.isVibroON = false
                binding.vibroSwitch.setImageResource(R.drawable.off_button)
                binding.vibroValueText.setText(R.string.off)
            } else {
                MainActivity.isVibroON = true
                binding.vibroSwitch.setImageResource(R.drawable.on_button)
                binding.vibroValueText.setText(R.string.on)
            }
        }
    }
}