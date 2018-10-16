/*
 * Copyright (c) 2018 André Sousa.
 */
package com.andrefrsousa.superbottomsheet

import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt

internal fun hasMinimumSdk(minimumSdk: Int): Boolean {
    return Build.VERSION.SDK_INT >= minimumSdk
}

@ColorInt
internal fun blendColors(@ColorInt from: Int, @ColorInt to: Int, ratio: Float): Int {
    val inverseRatio = 1f - ratio

    val r = Color.red(to) * ratio + Color.red(from) * inverseRatio
    val g = Color.green(to) * ratio + Color.green(from) * inverseRatio
    val b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio

    return Color.rgb(r.toInt(), g.toInt(), b.toInt())
}
