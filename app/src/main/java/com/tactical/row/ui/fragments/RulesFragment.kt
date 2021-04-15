package com.tactical.row.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.tactical.row.R
import com.tactical.row.databinding.FragmentRulesBinding

class RulesFragment : Fragment(R.layout.fragment_rules) {
    private var _binding: FragmentRulesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startButton.setOnClickListener {
            parentFragmentManager.commit {
                replace<LevelFragment>(R.id.fragment_container_view)
                addToBackStack(null)
            }
        }
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.settingsButton.setOnClickListener {
            parentFragmentManager.commit {
                replace<SettingsFragment>(R.id.fragment_container_view)
                addToBackStack(null)
            }
        }
    }
}