package com.mitsuru.insight.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.mitsuru.insight.R

class LoginButton : AppCompatButton {
    private lateinit var enableBG: Drawable
    private lateinit var disableBG: Drawable
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enableBG = ContextCompat.getDrawable(context, R.drawable.button_enable) as Drawable
        disableBG = ContextCompat.getDrawable(context, R.drawable.button_disable) as Drawable
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = enableBG

        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
        text = "Masuk"

    }
}