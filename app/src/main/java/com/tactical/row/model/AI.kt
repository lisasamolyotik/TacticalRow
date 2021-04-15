package com.tactical.row.model

import com.tactical.row.util.Constants
import kotlin.math.floor

class IA {
    private var DEPTH = Constants.DEPTH_EASY

    fun setDEPTH(DEPTH: Int) {
        this.DEPTH = DEPTH
    }

    fun getColumn(game: Array<Array<String?>>): Int {
        // Define the root node
        val root = Node()
        root.root = true

        // Create a tree with all possible games
        analyzeFuturePosition(root, DEPTH, game, Constants.COMPUTER)

        // Return the best column to play by the IA
        return getBestColumn(root)
    }

    fun playerWin(game: Array<Array<String?>>, player: String): Boolean {
        // Check if player win by column
        for (i in 0..6) {
            for (j in 0..2) {
                if (player == game[i][j] && player == game[i][j + 1] && player == game[i][j + 2] && player == game[i][j + 3]) {
                    return true
                }
            }
        }

        // Check if player win by line
        for (i in 0..3) {
            for (j in 0..5) {
                if (player == game[i][j] && player == game[i + 1][j] && player == game[i + 2][j] && player == game[i + 3][j]) {
                    return true
                }
            }
        }

        // Check if player win by right diagonal
        for (i in 0..3) {
            for (j in 3..5) {
                if (player == game[i][j] && player == game[i + 1][j - 1] && player == game[i + 2][j - 2] && player == game[i + 3][j - 3]) {
                    return true
                }
            }
        }

        // Check if player win by left diagonal
        for (i in 3..6) {
            for (j in 3..5) {
                if (player == game[i][j] && player == game[i - 1][j - 1] && player == game[i - 2][j - 2] && player == game[i - 3][j - 3]) {
                    return true
                }
            }
        }
        return false
    }

    private fun analyzeFuturePosition(
        node: Node,
        depth: Int,
        game: Array<Array<String?>>,
        player: String
    ) {
        if (playerWin(game, Constants.COMPUTER)) {
            node.estimation = MAX + MAX
            node.depth = depth
            return
        }
        if (playerWin(game, Constants.PLAYER)) {
            node.estimation = -MAX - MAX
            node.depth = depth
            return
        }
        if (depth == 0) {
            node.estimation = estimateGame(game)
            return
        }
        for (col in 0..6) {
            val saveOfGame = Array(7) {
                arrayOfNulls<String>(
                    6
                )
            }
            val nbPiecesByCol = numberOfPiecesByColumn(game)
            for (i in 0..6) {
                System.arraycopy(game[i], 0, saveOfGame[i], 0, 6)
            }
            if (nbPiecesByCol[col] < 6) {
                val position = (5 - nbPiecesByCol[col]) * 7 + col
                val line = floor((position / 7).toDouble()).toInt()
                saveOfGame[col][line] = player
                val no = Node(col = col, depth = depth, parent = node)
                node.nodes.add(no)
                analyzeFuturePosition(no, depth - 1, saveOfGame, getFuturePlayer(player))
            }
        }
    }

    private fun estimateGame(game: Array<Array<String?>>): Int {
        val computer_estimation = gameValue(game, Constants.COMPUTER) + winInOneShot(
            game,
            Constants.COMPUTER
        ) + winInTwoShots(game, Constants.COMPUTER)
        val human_estimation = gameValue(game, Constants.PLAYER) + winInOneShot(
            game,
            Constants.PLAYER
        ) + winInTwoShots(game, Constants.PLAYER)
        return computer_estimation - human_estimation
    }

    private fun getBestColumn(root: Node): Int {
        var columnToPlay = 0
        for (n in root.nodes) {
            buildTree(n)
        }
        var max = -9999999
        columnToPlay = root.nodes[0].col
        for (n in root.nodes) {
            val currentEstimation: Int = n.estimation
            val currentCol: Int = n.col
            if (currentEstimation > max
                || currentEstimation == max && (currentCol == 2 || currentCol == 3 || currentCol == 4)
            ) {
                max = currentEstimation
                columnToPlay = currentCol
            }
        }
        return columnToPlay
    }

    private fun buildTree(node: Node) {
        for (n in node.nodes) {
            if (n.estimation == 0 && n.nodes.size > 0) {
                buildTree(n)
            }
        }
        if (node.depth % 2 == 0) {
            var min = 9999999
            for (n in node.nodes) {
                if (n.estimation < min) {
                    min = n.estimation
                    node.estimation = min
                }
            }
        } else {
            var max = -9999999
            for (n in node.nodes) {
                if (n.estimation > max) {
                    max = n.estimation
                    node.estimation = max
                }
            }
        }
    }

    private fun getFuturePlayer(player: String): String {
        return if (player == Constants.COMPUTER) Constants.PLAYER else Constants.COMPUTER
    }

    private fun numberOfPiecesByColumn(game: Array<Array<String?>>): IntArray {
        // Return grid
        val grid = IntArray(7)

        // Initiate values to 0
        for (i in 0..6) {
            grid[i] = 0
        }

        // Calculate the number of the pieces by column
        for (i in 0..6) {
            for (j in 0..5) {
                if (game[i][j] != null) {
                    grid[i] += 1
                }
            }
        }
        return grid
    }

    private fun gameValue(game: Array<Array<String?>>, player: String): Int {
        // return value after analyze
        var value = 0

        // Recover values foreach pieces of the game
        for (i in 0..6) {
            for (j in 0..5) {
                if (player == game[i][j]) {
                    value += POSITIONS_VALUES[j][i]
                }
            }
        }
        return value
    }

    private fun winInTwoShots(game: Array<Array<String?>>, player: String): Int {
        // return value after analyze
        var value = 0

        // Recover columns values
        for (i in 0..6) {
            for (j in 0..2) {
                if (player == game[i][j] && player == game[i][j + 1] && game[i][j + 2] == null && game[i][j + 3] == null
                    || game[i][j] == null && game[i][j + 1] == null && player == game[i][j + 2] && player == game[i][j + 3]
                    || player == game[i][j] && game[i][j + 1] == null && game[i][j + 2] == null && player == game[i][j + 3]
                ) {
                    value += 300
                }
            }
        }

        // Recover line values
        for (i in 0..3) {
            for (j in 0..5) {
                if (player == game[i][j] && player == game[i + 1][j] && game[i + 2][j] == null && game[i + 3][j] == null
                    || game[i][j] == null && game[i + 1][j] == null && player == game[i + 2][j] && player == game[i + 3][j]
                    || player == game[i][j] && game[i + 1][j] == null && game[i + 2][j] == null && player == game[i + 3][j]
                ) {
                    value += 300
                }
            }
        }

        // Recover diagonal values from the right
        for (i in 0..3) {
            for (j in 3..5) {
                if (player == game[i][j] && player == game[i + 1][j - 1] && game[i + 2][j - 2] == null && game[i + 3][j - 3] == null
                    || game[i][j] == null && game[i + 1][j - 1] == null && player == game[i + 2][j - 2] && player == game[i + 3][j - 3]
                    || player == game[i][j] && game[i + 1][j - 1] == null && game[i + 2][j - 2] == null && player == game[i + 3][j - 3]
                ) {
                    value += 300
                }
            }
        }

        // Recover diagonal values from the left
        for (i in 3..6) {
            for (j in 3..5) {
                if (game[i][j] == null && game[i - 1][j - 1] == null && player == game[i - 2][j - 2] && player == game[i - 3][j - 3]
                    || player == game[i][j] && player == game[i - 1][j - 1] && game[i - 2][j - 2] == null && game[i - 3][j - 3] == null
                    || player == game[i][j] && game[i - 1][j - 1] == null && game[i - 2][j - 2] == null && player == game[i - 3][j - 3]
                ) {
                    value += 300
                }
            }
        }
        return value
    }

    private fun winInOneShot(game: Array<Array<String?>>, player: String): Int {
        // return value after analyze
        var value = 0

        // Recover columns values
        for (i in 0..6) {
            for (j in 0..2) {
                if (game[i][j] == null && player == game[i][j + 1] && player == game[i][j + 2] && player == game[i][j + 3]
                    || player == game[i][j] && game[i][j + 1] == null && player == game[i][j + 2] && player == game[i][j + 3]
                    || player == game[i][j] && player == game[i][j + 1] && game[i][j + 2] == null && player == game[i][j + 3]
                    || player == game[i][j] && player == game[i][j + 1] && player == game[i][j + 2] && game[i][j + 3] == null
                ) {
                    value += 5000
                }
            }
        }

        // Recover line values
        for (i in 0..3) {
            for (j in 0..5) {
                if (game[i][j] == null && player == game[i + 1][j] && player == game[i + 2][j] && player == game[i + 3][j]
                    || player == game[i][j] && game[i + 1][j] == null && player == game[i + 2][j] && player == game[i + 3][j]
                    || player == game[i][j] && player == game[i + 1][j] && game[i + 2][j] == null && player == game[i + 3][j]
                    || player == game[i][j] && player == game[i + 1][j] && player == game[i + 2][j] && game[i + 3][j] == null
                ) {
                    value += 5000
                }
            }
        }

        // Recover diagonal values from the right
        for (i in 0..3) {
            for (j in 3..5) {
                if (game[i][j] == null && player == game[i + 1][j - 1] && player == game[i + 2][j - 2] && player == game[i + 3][j - 3]
                    || player == game[i][j] && game[i + 1][j - 1] == null && player == game[i + 2][j - 2] && player == game[i + 3][j - 3]
                    || player == game[i][j] && player == game[i + 1][j - 1] && game[i + 2][j - 2] == null && player == game[i + 3][j - 3]
                    || player == game[i][j] && player == game[i + 1][j - 1] && player == game[i + 2][j - 2] && game[i + 3][j - 3] == null
                ) {
                    value += 5000
                }
            }
        }

        // Recover diagonal values from the left
        for (i in 3..6) {
            for (j in 3..5) {
                if (game[i][j] == null && player == game[i - 1][j - 1] && player == game[i - 2][j - 2] && player == game[i - 3][j - 3]
                    || player == game[i][j] && game[i - 1][j - 1] == null && player == game[i - 2][j - 2] && player == game[i - 3][j - 3]
                    || player == game[i][j] && player == game[i - 1][j - 1] && game[i - 2][j - 2] == null && player == game[i - 3][j - 3]
                    || player == game[i][j] && player == game[i - 1][j - 1] && player == game[i - 2][j - 2] && game[i - 3][j - 3] == null
                ) {
                    value += 5000
                }
            }
        }
        return value
    }

    companion object {
        private val POSITIONS_VALUES = arrayOf(
            intArrayOf(3, 4, 5, 7, 5, 4, 3),
            intArrayOf(4, 6, 8, 10, 8, 6, 4),
            intArrayOf(5, 8, 11, 13, 11, 8, 5),
            intArrayOf(5, 8, 11, 13, 11, 8, 5),
            intArrayOf(4, 6, 8, 10, 8, 6, 4),
            intArrayOf(3, 4, 5, 7, 5, 4, 3)
        )
        private const val MAX = 100000
    }
}