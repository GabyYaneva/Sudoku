package com.example.sudoku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.logic.SudokuGenerator
import com.example.sudoku.logic.SudokuValidator
import com.example.sudoku.model.Cell
import com.example.sudoku.model.SudokuBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SudokuViewModel : ViewModel() {

    private var savedPrefs: android.content.SharedPreferences? = null

    fun initPrefs(prefs: android.content.SharedPreferences) {
        savedPrefs = prefs
    }

    fun saveGame(difficulty: SudokuGenerator.Difficulty) {
        val prefs = savedPrefs ?: return
        val editor = prefs.edit()

        for (row in 0..8) {
            for (col in 0..8) {
                val cell = board.getCell(row, col)
                editor.putInt("cell_${row}_${col}", cell.value)
                editor.putBoolean("locked_${row}_${col}", cell.isLocked)
                editor.putBoolean("error_${row}_${col}", cell.isError)
            }
        }

        for (row in 0..8) {
            for (col in 0..8) {
                editor.putInt("sol_${row}_${col}", solution[row][col])
            }
        }

        editor.putInt("mistakes", _mistakes.value ?: 0)
        editor.putString("difficulty", difficulty.name)
        editor.putBoolean("has_saved_game", true)
        editor.apply()
    }

    fun loadGame() {
        val prefs = savedPrefs ?: return
        _isLoading.value = true

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                for (row in 0..8) {
                    for (col in 0..8) {
                        solution[row][col] = prefs.getInt("sol_${row}_${col}", 0)
                    }
                }

                for (row in 0..8) {
                    for (col in 0..8) {
                        val cell = board.getCell(row, col)
                        cell.value = prefs.getInt("cell_${row}_${col}", 0)
                        cell.isLocked = prefs.getBoolean("locked_${row}_${col}", false)
                        cell.isError = prefs.getBoolean("error_${row}_${col}", false)
                    }
                }
            }

            _mistakes.value = prefs.getInt("mistakes", 0)
            _cells.value = board.grid
            _isLoading.value = false
        }
    }
    private val board = SudokuBoard()
    private val validator = SudokuValidator()
    private var solution = Array(9) { IntArray(9) }

    private var selectedRow = -1
    private var selectedCol = -1
    private var isNotesMode = false

    private val undoStack = ArrayDeque<UndoAction>()

    data class UndoAction(
        val row: Int,
        val col: Int,
        val previousValue: Int,
        val previousNotes: Set<Int>,
        val wasError: Boolean
    )

    private val _cells = MutableLiveData<Array<Array<Cell>>>()
    val cells: LiveData<Array<Array<Cell>>> = _cells

    private val _mistakes = MutableLiveData(0)
    val mistakes: LiveData<Int> = _mistakes

    private val _isGameOver = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> = _isGameOver

    private val _isGameWon = MutableLiveData(false)
    val isGameWon: LiveData<Boolean> = _isGameWon

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _notesModeActive = MutableLiveData(false)
    val notesModeActive: LiveData<Boolean> = _notesModeActive

    fun startNewGame(difficulty: SudokuGenerator.Difficulty) {
        _isLoading.value = true
        _isGameOver.value = false
        _isGameWon.value = false
        _mistakes.value = 0
        undoStack.clear()

        viewModelScope.launch {
            val generator = SudokuGenerator()
            val (puzzle, sol) = withContext(Dispatchers.Default) {
                generator.generate(difficulty)
            }

            solution = sol
            board.loadPuzzle(puzzle, sol)
            selectedRow = -1
            selectedCol = -1

            _cells.value = board.grid
            _isLoading.value = false
        }
    }

    fun selectCell(row: Int, col: Int) {
        if (_isGameOver.value == true) return

        selectedRow = row
        selectedCol = col
        board.updateHighlights(row, col)
        _cells.value = board.grid
    }

    fun enterNumber(num: Int) {
        if (_isGameOver.value == true) return
        if (selectedRow == -1 || selectedCol == -1) return

        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isLocked) return

        if (isNotesMode) {
            saveToUndo(cell)
            if (cell.notes.contains(num)) {
                cell.notes.remove(num)
            } else {
                cell.notes.add(num)
            }
            cell.value = 0
            cell.isError = false
        } else {
            saveToUndo(cell)
            cell.notes.clear()
            cell.value = num

            if (solution[selectedRow][selectedCol] != num) {
                cell.isError = true
                incrementMistakes()
            } else {
                cell.isError = false
                checkWinCondition()
            }
        }

        _cells.value = board.grid
    }

    private fun incrementMistakes() {
        val current = (_mistakes.value ?: 0) + 1
        _mistakes.value = current

        if (current >= 3) {
            _isGameOver.value = true
        }
    }

    private fun checkWinCondition() {
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = board.getCell(row, col)
                if (cell.value == 0 || cell.isError) return
            }
        }
        _isGameWon.value = true
    }

    fun eraseCell() {
        if (_isGameOver.value == true) return
        if (selectedRow == -1 || selectedCol == -1) return

        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isLocked) return

        saveToUndo(cell)
        board.clearCell(selectedRow, selectedCol)
        _cells.value = board.grid
    }

    fun useHint() {
        if (_isGameOver.value == true) return
        if (selectedRow == -1 || selectedCol == -1) return

        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isLocked) return

        saveToUndo(cell)
        val correct = solution[selectedRow][selectedCol]
        cell.value = correct
        cell.isError = false
        cell.isLocked = true
        cell.notes.clear()

        checkWinCondition()
        _cells.value = board.grid
    }

    fun toggleNotesMode() {
        isNotesMode = !isNotesMode
        _notesModeActive.value = isNotesMode
    }

    fun undo() {
        if (_isGameOver.value == true) return
        if (undoStack.isEmpty()) return

        val action = undoStack.removeLast()
        val cell = board.getCell(action.row, action.col)

        if (cell.isError && !action.wasError) {
            _mistakes.value = (_mistakes.value ?: 1) - 1
        }

        cell.value = action.previousValue
        cell.notes = action.previousNotes.toMutableSet()
        cell.isError = action.wasError

        _cells.value = board.grid
    }

    private fun saveToUndo(cell: Cell) {
        undoStack.addLast(
            UndoAction(
                row = cell.row,
                col = cell.col,
                previousValue = cell.value,
                previousNotes = cell.notes.toSet(),
                wasError = cell.isError
            )
        )
        if (undoStack.size > 50) undoStack.removeFirst()
    }

    fun getSelectedRow() = selectedRow
    fun getSelectedCol() = selectedCol
    fun isCellSelected() = selectedRow != -1 && selectedCol != -1


}