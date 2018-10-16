/*
 * Copyright (c) 2018 André Sousa.
 */
package com.andrefrsousa.superbottomsheet

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout


internal class CornerRadiusFrameLayout : FrameLayout {

    private lateinit var path: Path
    private lateinit var rect: RectF
    private var backgroundOuterRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        path = Path()
        rect = RectF()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        path.reset()
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        path.addRoundRect(rect, backgroundOuterRadii, Path.Direction.CW)
        path.close()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }

    internal fun setCornerRadius(radius: Float) {
        // Top left corner
        backgroundOuterRadii[0] = radius
        backgroundOuterRadii[1] = radius

        // Top right corner
        backgroundOuterRadii[2] = radius
        backgroundOuterRadii[3] = radius

        path.reset()
        path.addRoundRect(rect, backgroundOuterRadii, Path.Direction.CW)
        path.close()

        invalidate()
    }
}