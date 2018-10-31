/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 André Sousa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.andrefrsousa.superbottomsheet

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout

internal class CornerRadiusFrameLayout : FrameLayout {

    // Variables
    private var noCornerRadius = true
    private val path = Path()
    private val rect = RectF()
    private val outerRadii = floatArrayOf(
            // Top left corner
            0f, 0f,
            // Top right corner
            0f, 0f,
            // Bottom right corner
            0f, 0f,
            // Bottom left corner
            0f, 0f
    )

    // Constructor
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        resetPath()
    }

    override fun draw(canvas: Canvas?) = when {
        noCornerRadius -> super.draw(canvas)

        else -> with(canvas!!) {
            val save = save()
            clipPath(path)

            super.draw(this)
            restoreToCount(save)
        }
    }

    //region PUBLIC METHODS

    internal fun setCornerRadius(radius: Float) {
        // Top left corner
        outerRadii[0] = radius
        outerRadii[1] = radius

        // Top right corner
        outerRadii[2] = radius
        outerRadii[3] = radius

        noCornerRadius = (radius == 0f)

        if (width == 0 || height == 0) {
            // Discard invalid events
            return
        }

        resetPath()
        invalidate()
    }

    //endregion

    //region PRIVATE METHODS

    private fun initView() {
        if (hasMaximumSdk(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    private fun resetPath() = path.run {
        reset()
        addRoundRect(rect, outerRadii, Path.Direction.CW)
        close()
    }

    //endregion
}