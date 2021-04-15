package com.tactical.row.adapter

import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.tactical.row.R
import com.tactical.row.model.IA
import com.tactical.row.ui.fragments.LevelFragment
import com.tactical.row.util.Constants
import java.text.DecimalFormat

class GridAdapter(val fragment: LevelFragment, firstPlayer: String, screenWidth: Int) :
    BaseAdapter() {
    private val mIA: IA
    private val mPiecesPlayed = Array(7) {
        arrayOfNulls<String>(
            6
        )
    }
    private val mNumberOfPiecesByColumn = IntArray(7)
    private val mThumbs = IntArray(42)
    private var nextPlayer: String
    private var colorPieceUser: String? = null

    private var mListener: GridAdapterListener? = null

    interface GridAdapterListener {
        fun onBeginComputerLoad()
        fun onFinishComputerLoad()
    }

    fun setDepthToIA(depth: Int) {
        mIA.setDEPTH(depth)
    }

    fun setColorPieceUser(colorPieceUser: String?) {
        this.colorPieceUser = colorPieceUser
    }

    fun gameHasBegin(): Boolean {
        var b = false
        for (i in 0..6) {
            if (mNumberOfPiecesByColumn[i] > 0) {
                b = true
            }
        }
        return b
    }

    override fun getCount(): Int {
        return mThumbs.size
    }

    override fun getItem(position: Int): Int {
        return mThumbs[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val image = ImageView(parent?.context)
        image.setImageResource(mThumbs[position])
        view?.layoutParams?.width = parent?.width!! / 7
        view?.layoutParams?.height = parent.width / 7
        image.adjustViewBounds = true
        image.requestLayout()
        return image
    }

    private fun cleanGrid() {
        for (i in 0..41) {
            mThumbs[i] = R.drawable.transparent
        }
        for (i in 0..6) {
            mNumberOfPiecesByColumn[i] = 0
        }
        for (i in 0..6) {
            for (z in 0..5) {
                mPiecesPlayed[i][z] = null
            }
        }
    }

    fun placeGamerPiece(position: Int) {
        if (!gameEnd()) {
            val column = position % 7
            if (mNumberOfPiecesByColumn[column] < 6) {
                var li = 5
                var b = false
                do {
                    if (mPiecesPlayed[column][li] == null) {
                        b = true
                        mNumberOfPiecesByColumn[column] = mNumberOfPiecesByColumn[column] + 1
                        mPiecesPlayed[column][li] = Constants.PLAYER
                        val positionAjouer = column + li * 7
                        if (Constants.GREEN_BALL.equals(colorPieceUser)) {
                            mThumbs[positionAjouer] = R.drawable.blue_ball
                        } else if (Constants.BLUE_BALL.equals(colorPieceUser)) {
                            mThumbs[positionAjouer] = R.drawable.green_ball
                        }
                        notifyDataSetChanged()
                        if (!mIA.playerWin(mPiecesPlayed, Constants.PLAYER)) {
                            if (stillPlay()) {
                                nextPlayer = Constants.COMPUTER
                                placeIAPiece()
                            } else {
                                fragment.openTryAgainFragment()
                                //equal
                            }
                        } else {
                            fragment.openYouWinFragment()
                            //win
                        }
                    } else {
                        li--
                    }
                } while (!b)
            } else {
                //no more space
                fragment.openTryAgainFragment()
            }
        }
    }

    private fun placeIAPiece() {
        object : AsyncTask<Void?, Void?, Int>() {
            private var startTime = 0.0
            override fun onPreExecute() {
                startTime = System.nanoTime().toDouble()
                mListener!!.onBeginComputerLoad()
            }

            override fun doInBackground(vararg params: Void?): Int? {
                var column = -1
                if (gameEnd()) {
                    //game over
                } else {
                    column = mIA.getColumn(mPiecesPlayed)
                    val df = DecimalFormat()
                    df.maximumFractionDigits = 5
                    val exectime = (System.nanoTime() - startTime) / 1000000000
                    Log.i("Execution time", exectime.toString() + "s.")
                    val restTime = java.lang.Double.valueOf((0.5 - exectime) * 1000).toLong()
                    if (restTime > 0) {
                        Log.i("Rest time", restTime.toString() + "ms.")
                        try {
                            Thread.sleep(restTime)
                        } catch (e: InterruptedException) {
                            Log.e("Err", e.message!!)
                        }
                    }
                }
                return column
            }

            override fun onPostExecute(column: Int) {
                if (column != -1) {
                    val positionAjouer = (5 - mNumberOfPiecesByColumn[column]) * 7 + column
                    val ligne = Math.floor((positionAjouer / 7).toDouble()).toInt()
                    mNumberOfPiecesByColumn[column] = mNumberOfPiecesByColumn[column] + 1
                    mPiecesPlayed[column][ligne] = Constants.COMPUTER
                    if (Constants.GREEN_BALL.equals(colorPieceUser)) {
                        mThumbs[positionAjouer] = R.drawable.green_ball
                    } else if (Constants.BLUE_BALL.equals(colorPieceUser)) {
                        mThumbs[positionAjouer] = R.drawable.blue_ball
                    }
                    notifyDataSetChanged()
                    if (!mIA.playerWin(mPiecesPlayed, nextPlayer)) {
                        if (stillPlay()) {
                            nextPlayer = Constants.PLAYER
                        } else {
                            //equal
                            fragment.openTryAgainFragment()
                        }
                    } else {
                        fragment.openTryAgainFragment()
                    }
                }
                mListener!!.onFinishComputerLoad()
            }
        }.execute()
    }

    fun gameEnd(): Boolean {
        return mIA.playerWin(mPiecesPlayed, Constants.COMPUTER) || mIA.playerWin(
            mPiecesPlayed,
            Constants.PLAYER
        ) || !stillPlay()
    }

    private fun stillPlay(): Boolean {
        var b = false
        for (i in 0..6) {
            if (mNumberOfPiecesByColumn[i] < 6) {
                b = true
                break
            }
        }
        return b
    }

    init {
        nextPlayer = firstPlayer
        mIA = IA()
        mListener = fragment
        cleanGrid()
    }
}
