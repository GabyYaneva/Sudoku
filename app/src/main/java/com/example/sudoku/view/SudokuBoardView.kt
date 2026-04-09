package com.example.sudoku.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.sudoku.model.Cell

class SudokuBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var cellSize = 0f
    private var boardSize = 0f

    var onCellSelected: ((row: Int, col: Int) -> Unit)? = null

    private var cells: Array<Array<Cell>> = Array(9) { row ->
        Array(9) { col -> Cell(row, col) }
    }

    var colorBackground = 0xFFFFFFFF.toInt()
    var colorAccent     = 0xFFADD8E6.toInt()
    var colorHighlight  = 0xFFE3F4F8.toInt()
    var colorError      = 0xFFFFCDD2.toInt()
    var colorBorderThin = 0xFFBDBDBD.toInt()
    var colorBorderThick= 0xFF424242.toInt()
    var colorLockedText = 0xFF1A1A2E.toInt()
    var colorPlayerText = 0xFF1565C0.toInt()
    var colorNoteText   = 0xFF757575.toInt()
    var colorErrorText  = 0xFFD32F2F.toInt()

    private val backgroundPaint = Paint()
    private val accentPaint     = Paint()
    private val highlightPaint  = Paint()
    private val errorPaint      = Paint()
    private val thinLinePaint   = Paint().apply {
        strokeWidth = 1f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val thickLinePaint  = Paint().apply {
        strokeWidth = 4f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val lockedTextPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    private val playerTextPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    private val errorTextPaint  = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    private val noteTextPaint   = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = measuredWidth
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        boardSize = w.toFloat()
        cellSize  = boardSize / 9f
        updateTextSizes()
    }

    private fun updateTextSizes() {
        lockedTextPaint.textSize = cellSize * 0.55f
        playerTextPaint.textSize = cellSize * 0.55f
        errorTextPaint.textSize  = cellSize * 0.55f
        noteTextPaint.textSize   = cellSize * 0.25f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        applyColors()
        drawCellBackgrounds(canvas)
        drawGridLines(canvas)
        drawNumbers(canvas)
    }

    private fun applyColors() {
        backgroundPaint.color = colorBackground
        accentPaint.color     = colorAccent
        highlightPaint.color  = colorHighlight
        errorPaint.color      = colorError
        thinLinePaint.color   = colorBorderThin
        thickLinePaint.color  = colorBorderThick
        lockedTextPaint.color = colorLockedText
        playerTextPaint.color = colorPlayerText
        errorTextPaint.color  = colorErrorText
        noteTextPaint.color   = colorNoteText
    }
    private fun drawCellBackgrounds(canvas: Canvas) {
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = cells[row][col]
                val left   = col * cellSize
                val top    = row * cellSize
                val right  = left + cellSize
                val bottom = top + cellSize
                val rect   = RectF(left, top, right, bottom)

                val paint = when {
                    cell.isError       -> errorPaint
                    cell.isSelected    -> accentPaint
                    cell.isHighlighted -> highlightPaint
                    else               -> backgroundPaint
                }
                canvas.drawRect(rect, paint)
            }
        }
    }
    private fun drawGridLines(canvas: Canvas) {
        for (i in 0..9) {
            val pos = i * cellSize
            val paint = if (i % 3 == 0) thickLinePaint else thinLinePaint
            canvas.drawLine(pos, 0f, pos, boardSize, paint)
            canvas.drawLine(0f, pos, boardSize, pos, paint)
        }
    }

    private fun drawNumbers(canvas: Canvas) {
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = cells[row][col]
                val centerX = col * cellSize + cellSize / 2f
                val centerY = row * cellSize + cellSize / 2f

                if (cell.value != 0) {
                    val paint = when {
                        cell.isError  -> errorTextPaint
                        cell.isLocked -> lockedTextPaint
                        else          -> playerTextPaint
                    }
                    val textBounds = Rect()
                    paint.getTextBounds(cell.value.toString(), 0, 1, textBounds)
                    canvas.drawText(
                        cell.value.toString(),
                        centerX,
                        centerY + textBounds.height() / 2f,
                        paint
                    )
                } else if (cell.notes.isNotEmpty()) {
                    drawNotes(canvas, cell, col * cellSize, row * cellSize)
                }
            }
        }
    }

    private fun drawNotes(canvas: Canvas, cell: Cell, left: Float, top: Float) {
        val noteSize = cellSize / 3f
        for (num in 1..9) {
            if (cell.notes.contains(num)) {
                val noteCol = (num - 1) % 3
                val noteRow = (num - 1) / 3
                val x = left + noteCol * noteSize + noteSize / 2f
                val y = top + noteRow * noteSize + noteSize / 2f

                val textBounds = Rect()
                noteTextPaint.getTextBounds(num.toString(), 0, 1, textBounds)
                canvas.drawText(
                    num.toString(),
                    x,
                    y + textBounds.height() / 2f,
                    noteTextPaint
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val col = (event.x / cellSize).toInt().coerceIn(0, 8)
            val row = (event.y / cellSize).toInt().coerceIn(0, 8)
            onCellSelected?.invoke(row, col)
            return true
        }
        return false
    }

    fun updateCells(newCells: Array<Array<Cell>>) {
        cells = newCells
        invalidate()
    }

    fun applyTheme(isDark: Boolean) {
        if (isDark) {
            colorBackground  = 0xFF1A1A2E.toInt()
            colorAccent      = 0xFF0F3460.toInt()
            colorHighlight   = 0xFF0D2137.toInt()
            colorError       = 0xFF8B3A00.toInt()
            colorBorderThin  = 0xFF424242.toInt()
            colorBorderThick = 0xFFE0E0E0.toInt()
            colorLockedText  = 0xFFE0E0E0.toInt()
            colorPlayerText  = 0xFF90CAF9.toInt()
            colorNoteText    = 0xFF9E9E9E.toInt()
            colorErrorText   = 0xFFFF6D00.toInt()
        } else {
            colorBackground  = 0xFFFFFFFF.toInt()
            colorAccent      = 0xFFADD8E6.toInt()
            colorHighlight   = 0xFFE3F4F8.toInt()
            colorError       = 0xFFFFCDD2.toInt()
            colorBorderThin  = 0xFFBDBDBD.toInt()
            colorBorderThick = 0xFF424242.toInt()
            colorLockedText  = 0xFF1A1A2E.toInt()
            colorPlayerText  = 0xFF1565C0.toInt()
            colorNoteText    = 0xFF757575.toInt()
            colorErrorText   = 0xFFD32F2F.toInt()
        }
        invalidate()
    }
}