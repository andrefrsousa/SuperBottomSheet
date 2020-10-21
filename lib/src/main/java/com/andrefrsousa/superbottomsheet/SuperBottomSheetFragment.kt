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

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class SuperBottomSheetFragment : BottomSheetDialogFragment() {

    internal lateinit var sheetTouchOutsideContainer: View
    internal lateinit var sheetContainer: CornerRadiusFrameLayout
    private lateinit var behavior: BottomSheetBehavior<*>
    private lateinit var callback: BottomSheetBehavior.BottomSheetCallback

    // Customizable properties
    private var propertyDim = 0f
    private var propertyCornerRadius = 0f
    private var propertyStatusBarColor = 0
    private var propertyIsAlwaysExpanded = false
    private var propertyIsSheetCancelableOnTouchOutside = true
    private var propertyIsSheetCancelable = true
    internal var propertyAnimateCornerRadius = true

    // Bottom sheet properties
    private var canSetStatusBarColor = false

    // region Methods from BottomSheetDialogFragment

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = if (animateStatusBar()) {
        SuperBottomSheetDialog(context, R.style.superBottomSheetDialog)

    } else SuperBottomSheetDialog(context)

    @CallSuper
    @SuppressLint("NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Change status bar on the condition: API >= 21
        val supportsStatusBarColor = hasMinimumSdk(Build.VERSION_CODES.LOLLIPOP)
        canSetStatusBarColor = !context.isTablet() && supportsStatusBarColor

        // Init properties
        propertyDim = getDim()
        propertyCornerRadius = getCornerRadius()
        propertyStatusBarColor = getStatusBarColor()
        propertyIsAlwaysExpanded = isSheetAlwaysExpanded()
        propertyIsSheetCancelable = isSheetCancelable()
        propertyIsSheetCancelableOnTouchOutside = isSheetCancelableOnTouchOutside()
        propertyAnimateCornerRadius = animateCornerRadius()

        // Set dialog properties
        dialog.runIfNotNull {
            setCancelable(propertyIsSheetCancelable)

            val isCancelableOnTouchOutside = propertyIsSheetCancelable && propertyIsSheetCancelableOnTouchOutside
            setCanceledOnTouchOutside(isCancelableOnTouchOutside)

            window.runIfNotNull {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setDimAmount(propertyDim)

                if (supportsStatusBarColor) {
                    @Suppress("DEPRECATION")
                    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    setStatusBarColor(1f)
                }

                if (context.isTablet() && !context.isInPortrait()) {
                    setGravity(Gravity.CENTER_HORIZONTAL)
                    setLayout(resources.getDimensionPixelSize(R.dimen.super_bottom_sheet_width), ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
        }

        return null
    }

    @CallSuper
    override fun onStart() {
        super.onStart()

        // Init UI components
        iniBottomSheetUiComponents()
    }

    override fun onResume() {
        super.onResume()
        behavior.addBottomSheetCallback(callback)
    }

    override fun onPause() {
        behavior.removeBottomSheetCallback(callback)
        super.onPause()
    }

    // endregion

    //region UI METHODS

    @UiThread
    private fun iniBottomSheetUiComponents() {
        // Store views references
        sheetContainer = dialog?.findViewById(R.id.super_bottom_sheet)!!
        sheetTouchOutsideContainer = dialog?.findViewById(R.id.touch_outside)!!

        // Set the bottom sheet radius
        sheetContainer.setBackgroundColor(getBackgroundColor())
        sheetContainer.setCornerRadius(propertyCornerRadius)

        // Load bottom sheet behaviour
        behavior = BottomSheetBehavior.from(sheetContainer)

        // Set tablet sheet width when in landscape. This will avoid full bleed sheet
        if (context.isTablet() && !context.isInPortrait()) {
            sheetContainer.layoutParams = sheetContainer.layoutParams.apply {
                width = resources.getDimensionPixelSize(R.dimen.super_bottom_sheet_width)
                height = getExpandedHeight()
            }
        }

        // If is always expanded, there is no need to set the peek height
        if (propertyIsAlwaysExpanded) {
            sheetContainer.layoutParams = sheetContainer.layoutParams.apply {
                height = getExpandedHeight()
            }

        } else {
            behavior.peekHeight = getPeekHeight()
            sheetContainer.minimumHeight = behavior.peekHeight
        }

        // Only skip the collapse state when the device is in landscape or the sheet is always expanded
        val deviceInLandscape = (!context.isTablet() && !context.isInPortrait()) || propertyIsAlwaysExpanded
        behavior.skipCollapsed = deviceInLandscape

        if (deviceInLandscape) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            setStatusBarColor(1f)

            // Load content container height
            sheetContainer.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (sheetContainer.height > 0) {
                        sheetContainer.viewTreeObserver.removeOnPreDrawListener(this)

                        // If the content sheet is expanded set the background and status bar properties
                        if (sheetContainer.height == sheetTouchOutsideContainer.height) {
                            setStatusBarColor(0f)

                            if (propertyAnimateCornerRadius) {
                                sheetContainer.setCornerRadius(0f)
                            }
                        }
                    }

                    return true
                }
            })
        }

        // Override sheet callback events
        callback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    setStatusBarColor(1f)
                    dialog?.cancel()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                setRoundedCornersOnScroll(bottomSheet, slideOffset)
                setStatusBarColorOnScroll(bottomSheet, slideOffset)
            }
        }
    }

    //region STATUS BAR

    @UiThread
    internal fun setStatusBarColorOnScroll(bottomSheet: View, slideOffset: Float) {
        if (!canSetStatusBarColor) return

        if (bottomSheet.height != sheetTouchOutsideContainer.height) {
            canSetStatusBarColor = false
            return
        }

        if (slideOffset.isNaN() || slideOffset <= 0) {
            setStatusBarColor(1f)
            return
        }

        val invertOffset = 1 - (1 * slideOffset)
        setStatusBarColor(invertOffset)
    }

    @SuppressLint("NewApi")
    @UiThread
    internal fun setStatusBarColor(dim: Float) {
        if (!canSetStatusBarColor) return

        val color = calculateColor(propertyStatusBarColor, dim)
        dialog?.window?.statusBarColor = color
    }

    //endregion

    //region CORNERS

    @UiThread
    internal fun setRoundedCornersOnScroll(bottomSheet: View, slideOffset: Float) {
        if (!propertyAnimateCornerRadius) return

        if (bottomSheet.height != sheetTouchOutsideContainer.height) {
            propertyAnimateCornerRadius = false
            return
        }

        if (slideOffset.isNaN() || slideOffset <= 0) {
            sheetContainer.setCornerRadius(propertyCornerRadius)
            return
        }

        if (propertyAnimateCornerRadius) {
            val radius = propertyCornerRadius - (propertyCornerRadius * slideOffset)
            sheetContainer.setCornerRadius(radius)
        }
    }

    //endregion

    //region PUBLIC

    open fun getPeekHeight() = with(context!!.getAttrId(R.attr.superBottomSheet_peekHeight)) {
        val peekHeightMin = when (this) {
            INVALID_RESOURCE_ID -> resources.getDimensionPixelSize(R.dimen.super_bottom_sheet_peek_height)
            else -> resources.getDimensionPixelSize(this)
        }

        with(resources.displayMetrics) {
            peekHeightMin.coerceAtLeast(heightPixels - heightPixels * 9 / 16)
        }
    }

    @Dimension
    open fun getDim() = with(context!!.getAttrId(R.attr.superBottomSheet_dim)) {
        when (this) {
            INVALID_RESOURCE_ID -> TypedValue().let {
                resources.getValue(R.dimen.super_bottom_sheet_dim, it, true)
                it.float
            }

            else -> TypedValue().let {
                resources.getValue(this, it, true)
                it.float
            }
        }
    }

    @ColorInt
    open fun getBackgroundColor() = with(context!!.getAttrId(R.attr.superBottomSheet_backgroundColor)) {
        when (this) {
            INVALID_RESOURCE_ID -> Color.WHITE
            else -> ContextCompat.getColor(context!!, this)
        }
    }

    @ColorInt
    open fun getStatusBarColor() = with(context!!.getAttrId(R.attr.superBottomSheet_statusBarColor)) {
        when (this) {
            INVALID_RESOURCE_ID -> ContextCompat.getColor(context!!, context!!.getAttrId(R.attr.colorPrimaryDark))
            else -> ContextCompat.getColor(context!!, this)
        }
    }

    @Dimension
    open fun getCornerRadius() = with(context!!.getAttrId(R.attr.superBottomSheet_cornerRadius)) {
        when (this) {
            INVALID_RESOURCE_ID -> context!!.resources.getDimension(R.dimen.super_bottom_sheet_radius)
            else -> resources.getDimension(this)
        }
    }

    open fun isSheetAlwaysExpanded() = with(context!!.getAttrId(R.attr.superBottomSheet_alwaysExpanded)) {
        when (this) {
            INVALID_RESOURCE_ID -> context!!.resources.getBoolean(R.bool.super_bottom_sheet_isAlwaysExpanded)
            else -> resources.getBoolean(this)
        }
    }

    open fun isSheetCancelableOnTouchOutside() = with(context!!.getAttrId(R.attr.superBottomSheet_cancelableOnTouchOutside)) {
        when (this) {
            INVALID_RESOURCE_ID -> context!!.resources.getBoolean(R.bool.super_bottom_sheet_cancelableOnTouchOutside)
            else -> resources.getBoolean(this)
        }
    }

    open fun isSheetCancelable() = with(context!!.getAttrId(R.attr.superBottomSheet_cancelable)) {
        when (this) {
            INVALID_RESOURCE_ID -> context!!.resources.getBoolean(R.bool.super_bottom_sheet_cancelable)
            else -> resources.getBoolean(this)
        }
    }

    @IntRange(from = ViewGroup.LayoutParams.WRAP_CONTENT.toLong(), to = ViewGroup.LayoutParams.MATCH_PARENT.toLong())
    open fun getExpandedHeight() = with(context!!.getAttrId(R.attr.superBottomSheet_expandedHeight)) {
        when (this) {
            INVALID_RESOURCE_ID -> context!!.resources.getInteger(R.integer.super_bottom_expanded_behaviour)
            else -> resources.getInteger(this)
        }
    }

    open fun animateCornerRadius() = with(context!!.getAttrId(R.attr.superBottomSheet_animateCornerRadius)) {
        when (this) {
            INVALID_RESOURCE_ID -> context!!.resources.getBoolean(R.bool.super_bottom_sheet_animate_corner_radius)
            else -> resources.getBoolean(this)
        }
    }

    open fun animateStatusBar() = with(context!!.getAttrId(R.attr.superBottomSheet_animateStatusBar)) {
        when (this) {
            INVALID_RESOURCE_ID -> context!!.resources.getBoolean(R.bool.super_bottom_sheet_animate_status_bar)
            else -> resources.getBoolean(this)
        }
    }

    //endregion
}