package com.example.sudoku.logic

class SudokuGenerator {

    private val solver = SudokuSolver()
    private val validator = SudokuValidator()

    enum class Difficulty(val cellsToRemove: Int) {
        EASY(33),
        MEDIUM(45),
        HARD(51)
    }

    fun generate(difficulty: Difficulty): Pair<Array<IntArray>, Array<IntArray>> {
        val board = Array(9) { IntArray(9) { 0 } }

        fillDiagonalBoxes(board)

        solver.solve(board)

        val solution = Array(9) { row -> board[row].copyOf() }

        removeCells(board, difficulty.cellsToRemove)

        return Pair(board, solution)
    }

    private fun fillDiagonalBoxes(board: Array<IntArray>) {
        for (box in 0..2) {
            fillBox(board, box * 3, box * 3)
        }
    }

    private fun fillBox(board: Array<IntArray>, rowStart: Int, colStart: Int) {
        val numbers = (1..9).toMutableList().also { it.shuffle() }
        var index = 0
        for (r in rowStart..rowStart + 2) {
            for (c in colStart..colStart + 2) {
                board[r][c] = numbers[index++]
            }
        }
    }
    private fun removeCells(board: Array<IntArray>, count: Int) {
        val positions = (0..80).toMutableList().also { it.shuffle() }
        var removed = 0

        for (pos in positions) {
            if (removed >= count) break

            val row = pos / 9
            val col = pos % 9

            if (board[row][col] == 0) continue

            val backup = board[row][col]
            board[row][col] = 0

            val copy = Array(9) { r -> board[r].copyOf() }
            if (solver.countSolutions(copy) == 1) {
                removed++
            } else {
                board[row][col] = backup
            }
        }
    }
}