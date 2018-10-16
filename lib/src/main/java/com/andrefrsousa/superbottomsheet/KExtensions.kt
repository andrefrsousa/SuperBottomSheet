/*
 * Copyright (c) 2018 André Sousa.
 */
package com.andrefrsousa.superbottomsheet

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View

//region NULL

internal inline fun <T, R> T?.runIfNotNull(block: T.() -> R): R? {
    return this?.block()
}

//endregion

//region VIEW

internal fun View.setBackgroundCompat(drawable: Drawable) {
    if (hasMinimumSdk(Build.VERSION_CODES.JELLY_BEAN)) {
        background = drawable
    } else {
        @Suppress("DEPRECATION")
        setBackgroundDrawable(drawable)
    }
}

//endregion

//region CONTEXT

internal fun Context?.isTablet(): Boolean {
    return this?.resources?.getBoolean(R.bool.super_bottom_sheet_isTablet) ?: false
}

internal fun Context?.isInPortrait(): Boolean {
    return this?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT
}

internal fun Context.getAttrId(attrId: Int): Int {
    val typedValue = TypedValue()

    if (!theme.resolveAttribute(attrId, typedValue, true)) {
        return INVALID_RESOURCE_ID
    }

    return typedValue.resourceId
}

//endregion