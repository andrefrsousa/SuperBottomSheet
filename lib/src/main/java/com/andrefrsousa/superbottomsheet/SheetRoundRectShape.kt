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

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.shapes.RectShape

internal class SheetRoundRectShape(radius: Float) : RectShape() {

    private var outerRadii = floatArrayOf(
            // Top left corner
            0f, 0f,
            // Top right corner
            0f, 0f,
            // Bottom right corner
            0f, 0f,
            // Bottom left corner
            0f, 0f
    )

    private val path: Path

    init {
        // Top left corner
        outerRadii[0] = radius
        outerRadii[1] = radius

        // Top right corner
        outerRadii[2] = radius
        outerRadii[3] = radius

        // init path
        path = Path()
    }

    internal fun setRadius(radius: Float) {
        // Top left corner
        outerRadii[0] = radius
        outerRadii[1] = radius

        // Top right corner
        outerRadii[2] = radius
        outerRadii[3] = radius

        // Reset the current path
        path.run {
            reset()
            addRoundRect(rect(), outerRadii, Path.Direction.CW)
            close()
        }
    }

    override fun draw(canvas: Canvas, paint: Paint) = canvas.drawPath(path, paint)

    override fun getOutline(outline: Outline) {
        val radius = outerRadii[0]

        for (i in 1..7) {
            if (outerRadii[i] != radius) {
                // can't call simple constructors, use path
                outline.setConvexPath(path)
                return
            }
        }

        val rect = rect()

        outline.setRoundRect(
                Math.ceil(rect.left.toDouble()).toInt(),
                Math.ceil(rect.top.toDouble()).toInt(),
                Math.floor(rect.right.toDouble()).toInt(),
                Math.floor(rect.bottom.toDouble()).toInt(),
                radius
        )
    }

    override fun onResize(w: Float, h: Float) {
        super.onResize(w, h)

        path.run {
            reset()
            addRoundRect(rect(), outerRadii, Path.Direction.CW)
            close()
        }
    }
}
