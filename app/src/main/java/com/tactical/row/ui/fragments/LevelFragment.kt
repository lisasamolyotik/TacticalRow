package com.tactical.row.ui.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.tactical.row.R
import com.tactical.row.adapter.GridAdapter
import com.tactical.row.databinding.FragmentLevelBinding
import com.tactical.row.util.Constants

class LevelFragment : Fragment(R.layout.fragment_level), GridAdapter.GridAdapterListener {
    private var _binding: FragmentLevelBinding? = null
    private val binding get() = _binding!!

    private var FIRST_PLAYER: String? = null
    private var DEPTH = 0
    private var COLOR_PIECE_USER: String? = null

    private var adapter: GridAdapter? = null

    private var gridView: GridView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevelBinding.inflate(inflater, container, false)
        DEPTH = Constants.DEPTH_EASY
        COLOR_PIECE_USER = Constants.GREEN_BALL
        FIRST_PLAYER = Constants.PLAYER

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridView = binding.gridView
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.settingsButton.setOnClickListener {
            parentFragmentManager.commit {
                replace<SettingsFragment>(R.id.fragment_container_view)
                addToBackStack(null)
            }
        }

        initOrResetGame()

        gridView!!.setOnItemClickListener { parent, view, position, id ->
            if (Constants.COMPUTER.equals(FIRST_PLAYER) && !adapter!!.gameHasBegin()) {
                //Snackbar.make(view, R.string.touch_play_for_begin, Snackbar.LENGTH_SHORT).show()
            } else {
                adapter!!.placeGamerPiece(position)
            }
        }
    }

    private fun initOrResetGame() {
        val metrics = DisplayMetrics()
        requireActivity().windowManager.getDefaultDisplay().getMetrics(metrics)
        adapter = GridAdapter(this, FIRST_PLAYER!!, metrics.widthPixels)
        adapter!!.setColorPieceUser(COLOR_PIECE_USER)
        adapter!!.setDepthToIA(DEPTH)

        gridView!!.adapter = adapter
    }

    fun openYouWinFragment() {
        parentFragmentManager.commit {
            add<YouWinFragment>(R.id.fragment_container_view)
        }
        blockButtons()
    }

    fun openTryAgainFragment() {
        parentFragmentManager.commit {
            add<TryAgainFragment>(R.id.fragment_container_view)
        }
        blockButtons()
    }

    override fun onBeginComputerLoad() {
        gridView!!.isEnabled = false
    }

    override fun onFinishComputerLoad() {
        gridView!!.isEnabled = true
    }

    private fun blockButtons() {
        binding.settingsButton.isClickable = false
        binding.backButton.isClickable = false
    }
}