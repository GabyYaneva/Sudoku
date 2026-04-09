package com.example.sudoku.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.R
import com.example.sudoku.databinding.ActivityGameBinding
import com.example.sudoku.logic.SudokuGenerator
import com.example.sudoku.viewmodel.SudokuViewModel


class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: SudokuViewModel by viewModels()

    private var isDarkTheme = false
    private var difficulty = SudokuGenerator.Difficulty.EASY

    private var timerSeconds = 0
    private var timerRunning = false
    private val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            timerSeconds++
            updateTimerDisplay()
            timerHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val diffName = intent.getStringExtra("DIFFICULTY") ?: "EASY"
        difficulty = SudokuGenerator.Difficulty.valueOf(diffName)
        val prefs = getSharedPreferences("sudoku_prefs", MODE_PRIVATE)
        viewModel.initPrefs(prefs)
        isDarkTheme = prefs.getBoolean("is_dark_theme", false)
        applyTheme()
        val shouldContinue = intent.getBooleanExtra("CONTINUE", false)

         if (shouldContinue) {
            viewModel.loadGame()
        } else {
            viewModel.startNewGame(difficulty)
        }

        setupBoardListener()
        setupNumberPanel()
        setupActionBar()
        observeViewModel()

    }

    private fun setupBoardListener() {
        binding.sudokuBoard.onCellSelected = { row, col ->
            viewModel.selectCell(row, col)
        }
    }

    private fun setupNumberPanel() {
        val buttons = listOf(
            binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9
        )
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.enterNumber(index + 1)
            }
        }
    }

    private fun setupActionBar() {
        binding.btnUndo.setOnClickListener {
            viewModel.undo()
        }

        binding.btnErase.setOnClickListener {
            viewModel.eraseCell()
        }

        binding.btnNotes.setOnClickListener {
            viewModel.toggleNotesMode()
        }

        binding.btnHint.setOnClickListener {
            viewModel.useHint()
        }
        binding.btnExit.setOnClickListener {
            viewModel.saveGame(difficulty)
            finish()
        }
    }

    private fun observeViewModel() {

        viewModel.cells.observe(this) { cells ->
            binding.sudokuBoard.updateCells(cells)
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility =
                if (loading) View.VISIBLE else View.GONE
            binding.sudokuBoard.visibility =
                if (loading) View.INVISIBLE else View.VISIBLE
            binding.numberPanel.visibility =
                if (loading) View.INVISIBLE else View.VISIBLE
            binding.actionBar.visibility =
                if (loading) View.INVISIBLE else View.VISIBLE

            if (!loading) startTimer()
        }

        viewModel.mistakes.observe(this) { mistakes ->
            binding.tvMistakes.text = getString(R.string.mistakes) + ": $mistakes/3"
            val color = if (mistakes >= 2)
                getColor(R.color.orange_error)
            else
                if (isDarkTheme)
                    getColor(R.color.dark_text)
                else
                    getColor(R.color.light_text)
            binding.tvMistakes.setTextColor(color)
        }

        viewModel.notesModeActive.observe(this) { active ->
            val tint = if (active)
                getColor(R.color.blue_player)
            else
                if (isDarkTheme)
                    getColor(R.color.dark_text)
                else
                    getColor(R.color.light_text)
            binding.btnNotes.imageTintList = ColorStateList.valueOf(tint)
        }

        viewModel.isGameOver.observe(this) { gameOver ->
            if (gameOver) {
                stopTimer()
                showGameOverDialog()
            }
        }

        viewModel.isGameWon.observe(this) { won ->
            if (won) {
                stopTimer()
                showWinDialog()
            }
        }
    }

    private fun startTimer() {
        timerSeconds = 0
        timerRunning = true
        timerHandler.post(timerRunnable)
    }

    private fun stopTimer() {
        timerRunning = false
        timerHandler.removeCallbacks(timerRunnable)
    }

    private fun updateTimerDisplay() {
        val minutes = timerSeconds / 60
        val seconds = timerSeconds % 60
        binding.tvTimer.text = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds) }

    private fun showGameOverDialog() {
        val minutes = timerSeconds / 60
        val seconds = timerSeconds % 60
        val timeStr = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Time: $timeStr\nMistakes: ${viewModel.mistakes.value}/3")
            .setPositiveButton("Play Again") { _, _ ->
                viewModel.startNewGame(difficulty)
                startTimer()
            }
            .setNegativeButton("New Game") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showWinDialog() {
        val minutes = timerSeconds / 60
        val seconds = timerSeconds % 60
        val timeStr = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
        AlertDialog.Builder(this)
            .setTitle("You Win! 🎉")
            .setMessage("Time: $timeStr\nMistakes: ${viewModel.mistakes.value}/3")
            .setPositiveButton("Play Again") { _, _ ->
                viewModel.startNewGame(difficulty)
                startTimer()
            }
            .setNegativeButton("Main Menu") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun applyTheme() {
        val bgColor = if (isDarkTheme)
            getColor(R.color.dark_background) else getColor(R.color.light_background)

        val textColor = if (isDarkTheme)
            getColor(R.color.dark_text) else getColor(R.color.light_text)

        val surfaceColor = if (isDarkTheme)
            getColor(R.color.dark_surface) else getColor(R.color.light_surface)

        val iconTint = ColorStateList.valueOf(textColor)
        val surfaceTint = ColorStateList.valueOf(surfaceColor)

        binding.gameLayout.setBackgroundColor(bgColor)

        binding.tvMistakes.setTextColor(textColor)
        binding.tvTimer.setTextColor(textColor)

        binding.btnExit.backgroundTintList = surfaceTint
        binding.btnExit.setTextColor(textColor)

        binding.btnUndo.imageTintList = iconTint
        binding.btnErase.imageTintList = iconTint
        binding.btnHint.imageTintList = iconTint
        binding.btnNotes.imageTintList = iconTint

        listOf(
            binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9
        ).forEach { it.setTextColor(textColor) }

        binding.sudokuBoard.applyTheme(isDarkTheme)
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("sudoku_prefs", MODE_PRIVATE)
        isDarkTheme = prefs.getBoolean("is_dark_theme", false)
        applyTheme()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}