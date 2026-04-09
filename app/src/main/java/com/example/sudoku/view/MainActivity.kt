package com.example.sudoku.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.tooling.preview.Preview
import com.example.sudoku.R
import com.example.sudoku.databinding.ActivityMainBinding
import com.example.sudoku.logic.SudokuGenerator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private var isDarkTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("sudoku_prefs", MODE_PRIVATE)
        isDarkTheme = prefs.getBoolean("is_dark_theme", false)

        applyTheme()
        setupThemeToggle()
        updateContinueButton()
        setupButtons()

/*        binding.mainLayout.setOnClickListener {
            android.widget.Toast.makeText(this,"Clicked",android.widget.Toast.LENGTH_SHORT).show()
        }

 */
    }

    override fun onResume() {
        super.onResume()
        updateContinueButton()
    }

    private fun setupButtons() {
        binding.btnEasy.setOnClickListener {
            clearSavedGame()
            startGame(SudokuGenerator.Difficulty.EASY)
        }
        binding.btnMedium.setOnClickListener {
            clearSavedGame()
            startGame(SudokuGenerator.Difficulty.MEDIUM)
        }
        binding.btnHard.setOnClickListener {
            clearSavedGame()
            startGame(SudokuGenerator.Difficulty.HARD)
        }
        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("CONTINUE", true)
            intent.putExtra(
                "DIFFICULTY",
                prefs.getString("saved_difficulty", "EASY")
            )
            startActivity(intent)
        }
    }

    private fun updateContinueButton() {
        val hasSavedGame = prefs.getBoolean("has_saved_game", false)
        binding.btnContinue.visibility = if (hasSavedGame)
            android.view.View.VISIBLE
        else
            android.view.View.GONE
    }

    private fun clearSavedGame() {
        prefs.edit()
            .putBoolean("has_saved_game", false)
            .apply()
    }

    private fun startGame(difficulty: SudokuGenerator.Difficulty) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("DIFFICULTY", difficulty.name)
        intent.putExtra("CONTINUE", false)
        startActivity(intent)
    }

    private fun setupThemeToggle() {
        val themeIcon = if (isDarkTheme) R.drawable.ic_brightness else R.drawable.ic_moon
        binding.btnTheme.setImageResource(themeIcon)

        binding.btnTheme.setOnClickListener {
            android.widget.Toast.makeText(this, "Theme clicked!", android.widget.Toast.LENGTH_SHORT).show()
            isDarkTheme = !isDarkTheme
            prefs.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
            applyTheme()
        }
    }

    private fun applyTheme() {
        val bgColor = if (isDarkTheme)
            getColor(R.color.dark_background) else getColor(R.color.light_background)

        val textColor = if (isDarkTheme)
            getColor(R.color.dark_text) else getColor(R.color.light_text)

        val accentColor = if (isDarkTheme)
            getColor(R.color.dark_accent) else getColor(R.color.light_accent)

        val surfaceColor = if (isDarkTheme)
            getColor(R.color.dark_surface) else getColor(R.color.light_surface)

        val themeIcon = if (isDarkTheme)
            R.drawable.ic_brightness else R.drawable.ic_moon

        val iconTint = android.content.res.ColorStateList.valueOf(textColor)
        val accentTint = android.content.res.ColorStateList.valueOf(accentColor)
        val surfaceTint = android.content.res.ColorStateList.valueOf(surfaceColor)

        binding.mainLayout.setBackgroundColor(bgColor)
        binding.appTitle.setTextColor(textColor)
        binding.selectLabel.setTextColor(textColor)

        binding.btnTheme.setImageResource(themeIcon)
        binding.btnTheme.imageTintList = iconTint

        binding.btnEasy.backgroundTintList = accentTint
        binding.btnMedium.backgroundTintList = accentTint
        binding.btnHard.backgroundTintList = accentTint
        binding.btnEasy.setTextColor(textColor)
        binding.btnMedium.setTextColor(textColor)
        binding.btnHard.setTextColor(textColor)

        binding.btnContinue.backgroundTintList = surfaceTint
        binding.btnContinue.setTextColor(textColor)
    }
}