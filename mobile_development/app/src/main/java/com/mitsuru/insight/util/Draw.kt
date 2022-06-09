package com.mitsuru.insight.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View

class Draw(context: Context?,var rect: Rect,var text: String): View(context) {

    lateinit var boundPaint: Paint
    lateinit var textPaint: Paint

    private fun init(){
        boundPaint = Paint()
        boundPaint.color = Color.RED
        boundPaint.strokeWidth = 8f
        boundPaint.style = Paint.Style.STROKE

        textPaint = Paint()
        textPaint.color = Color.BLACK
        textPaint.textSize = 45f
        textPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawText(text ,rect.centerX().toFloat(), rect.centerY().toFloat(), textPaint)
        canvas?.drawRect(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), boundPaint)
    }
}