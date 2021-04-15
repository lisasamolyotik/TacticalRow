package com.tactical.row.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.tactical.row.R
import com.tactical.row.databinding.FragmentYouWinBinding

class YouWinFragment : Fragment(R.layout.fragment_you_win) {
    private var _binding: FragmentYouWinBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYouWinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tryAgainButton.setOnClickListener {
            parentFragmentManager.popBackStack()
            parentFragmentManager.commit {
                replace<LevelFragment>(R.id.fragment_container_view)
                addToBackStack(null)
            }
        }
    }
}