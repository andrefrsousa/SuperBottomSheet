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
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.annotation.UiThread
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.*

abstract class SuperBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var backgroundShape: ShapeDrawable
    private lateinit var bottomSheet: CornerRadiusFrameLayout
    private lateinit var behavior: BottomSheetBehavior<*>

    // Customizable properties
    private var propertyDim = 0f
    private var propertyCornerRadius = 0f
    private var propertyStatusBarColor = 0
    private var propertyIsAlwaysExpanded = false
    private var propertyIsSheetCancelableOnTouchOutside = true
    private var propertyIsSheetCancelable = true
    private var propertyAnimateCornerRadius = true

    // Bottom sheet properties
    private var backgroundOuterRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var canSetStatusBarColor = false

    /** Methods from [BottomSheetDialogFragment]  */

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return SuperBottomSheetDialog(this.context!!, R.style.superBottomSheetDialog)
    }

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
        dialog.run {
            setCancelable(propertyIsSheetCancelable)

            val isCancelableOnTouchOutside = propertyIsSheetCancelable && propertyIsSheetCancelableOnTouchOutside
            setCanceledOnTouchOutside(isCancelableOnTouchOutside)
        }

        // Set window properties
        dialog.window.runIfNotNull {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(propertyDim)

            if (supportsStatusBarColor) {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                statusBarColor = Color.TRANSPARENT
            }

            if (context.isTablet() && !context.isInPortrait()) {
                setGravity(Gravity.CENTER_HORIZONTAL)
                setLayout(resources.getDimensionPixelSize(R.dimen.super_bottom_sheet_width), ViewGroup.LayoutParams.WRAP_CONTENT)
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

    //region UI METHODS

    @UiThread
    private fun iniBottomSheetUiComponents() {
        // Store views references
        bottomSheet = dialog.findViewById(R.id.super_bottom_sheet)
        val touchOutsideView = dialog.findViewById<View>(R.id.touch_outside)

        // Hack to find the bottom sheet holder view
        initBackgroundShape()

        // Load bottom sheet behaviour
        behavior = BottomSheetBehavior.from(bottomSheet)

        // Set background shape
        bottomSheet.setBackgroundCompat(backgroundShape)

        // Set tablet sheet width when in landscape. This will avoid full bleed sheet
        if (context.isTablet() && !context.isInPortrait()) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.width = resources.getDimensionPixelSize(R.dimen.super_bottom_sheet_width)
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            bottomSheet.layoutParams = layoutParams
        }

        // If is always expanded, there is no need to set the peek height
        if (!propertyIsAlwaysExpanded) {
            behavior.peekHeight = getPeekHeight()

            bottomSheet.run {
                minimumHeight = behavior.peekHeight
            }
        } else {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams
        }

        // Only skip the collapse state when the device is in landscape or the sheet is always expanded
        val deviceInLandscape = (!context.isTablet() && !context.isInPortrait()) || propertyIsAlwaysExpanded
        behavior.skipCollapsed = deviceInLandscape

        if (deviceInLandscape) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            setStatusBarColor(Color.TRANSPARENT)

            // Load content container height
            bottomSheet.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (bottomSheet.height > 0) {
                        bottomSheet.viewTreeObserver.removeOnPreDrawListener(this)

                        // If the content sheet is expanded set the background and status bar properties
                        if (bottomSheet.height == touchOutsideView.height) {
                            setStatusBarColor(0f)

                            if (propertyAnimateCornerRadius) {
                                setBackgroundShapeRadius(0f)
                            }
                        }
                    }

                    return true
                }
            })
        }

        // Override sheet callback events
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    setStatusBarColor(Color.TRANSPARENT)
                    dialog.cancel()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!canSetStatusBarColor) {
                    return
                }

                if (bottomSheet.height != touchOutsideView.height) {
                    canSetStatusBarColor = false
                    return
                }

                if (slideOffset.isNaN() || slideOffset <= 0) {
                    setStatusBarColor(Color.TRANSPARENT)
                    return
                }

                val dim = propertyDim - (propertyDim * slideOffset)
                setStatusBarColor(dim)

                if (propertyAnimateCornerRadius) {
                    val radius = propertyCornerRadius - (propertyCornerRadius * slideOffset)
                    setBackgroundShapeRadius(radius)
                }
            }
        })
    }

    //region STATUS BAR

    @UiThread
    private fun setStatusBarColor(dim: Float) {
        setStatusBarColor(blendColors(propertyStatusBarColor, Color.BLACK, dim))
    }

    @SuppressLint("NewApi")
    @UiThread
    private fun setStatusBarColor(@ColorInt color: Int) {
        if (!canSetStatusBarColor) {
            return
        }

        dialog.window!!.statusBarColor = color
    }

    //endregion

    //region BACKGROUND

    private fun initBackgroundShape() {
        backgroundShape = ShapeDrawable(getShape(propertyCornerRadius))

        backgroundShape.paint.run {
            color = getBackgroundColor()
            style = Paint.Style.FILL
            isAntiAlias = true
            flags = Paint.ANTI_ALIAS_FLAG
        }

        bottomSheet.setCornerRadius(propertyCornerRadius)
    }

    private fun setBackgroundShapeRadius(radius: Float) {
        backgroundShape.shape = getShape(radius)
        bottomSheet.setCornerRadius(radius)
    }

    private fun getShape(radius: Float): RoundRectShape {
        // Top left corner
        backgroundOuterRadii[0] = radius
        backgroundOuterRadii[1] = radius

        // Top right corner
        backgroundOuterRadii[2] = radius
        backgroundOuterRadii[3] = radius

        return RoundRectShape(backgroundOuterRadii, null, null)
    }

    //endregion

    //region PUBLIC

    @Dimension
    open fun getPeekHeight(): Int {
        val dimenId = context!!.getAttrId(R.attr.superBottomSheet_peekHeight)

        val peekHeightMin = if (dimenId == INVALID_RESOURCE_ID) {
            resources.getDimensionPixelSize(R.dimen.super_bottom_sheet_peek_height)
        } else {
            resources.getDimensionPixelSize(dimenId)
        }

        // 16:9 ratio
        val displayMetrics = resources.displayMetrics
        return Math.max(peekHeightMin, displayMetrics.heightPixels - displayMetrics.heightPixels * 9 / 16)
    }

    @Dimension
    open fun getDim(): Float {
        val floatId = context!!.getAttrId(R.attr.superBottomSheet_dim)

        if (floatId == INVALID_RESOURCE_ID) {
            val outValue = TypedValue()
            resources.getValue(R.dimen.super_bottom_sheet_dim, outValue, true)
            return outValue.float
        }

        val outValue = TypedValue()
        resources.getValue(floatId, outValue, true)
        return outValue.float
    }

    @ColorInt
    open fun getBackgroundColor(): Int {
        val colorId = context!!.getAttrId(R.attr.superBottomSheet_backgroundColor)

        if (colorId == INVALID_RESOURCE_ID) {
            return Color.WHITE
        }

        return ContextCompat.getColor(context!!, colorId)
    }

    @ColorInt
    open fun getStatusBarColor(): Int {
        val colorId = context!!.getAttrId(R.attr.superBottomSheet_statusBarColor)

        if (colorId == INVALID_RESOURCE_ID) {
            return ContextCompat.getColor(context!!, context!!.getAttrId(R.attr.colorPrimaryDark))
        }

        return ContextCompat.getColor(context!!, colorId)
    }

    @Dimension
    open fun getCornerRadius(): Float {
        val dimenId = context!!.getAttrId(R.attr.superBottomSheet_cornerRadius)

        if (dimenId == INVALID_RESOURCE_ID) {
            return context!!.resources.getDimension(R.dimen.super_bottom_sheet_radius)
        }

        return resources.getDimension(dimenId)
    }

    open fun isSheetAlwaysExpanded(): Boolean {
        val boolId = context!!.getAttrId(R.attr.superBottomSheet_alwaysExpanded)

        if (boolId == INVALID_RESOURCE_ID) {
            return context!!.resources.getBoolean(R.bool.super_bottom_sheet_isAlwaysExpanded)
        }

        return resources.getBoolean(boolId)
    }

    open fun isSheetCancelableOnTouchOutside(): Boolean {
        val boolId = context!!.getAttrId(R.attr.superBottomSheet_cancelableOnTouchOutside)

        if (boolId == INVALID_RESOURCE_ID) {
            return context!!.resources.getBoolean(R.bool.super_bottom_sheet_cancelableOnTouchOutside)
        }

        return resources.getBoolean(boolId)
    }

    open fun isSheetCancelable(): Boolean {
        val boolId = context!!.getAttrId(R.attr.superBottomSheet_cancelable)

        if (boolId == INVALID_RESOURCE_ID) {
            return context!!.resources.getBoolean(R.bool.super_bottom_sheet_cancelable)
        }

        return resources.getBoolean(boolId)
    }

    open fun animateCornerRadius(): Boolean {
        val boolId = context!!.getAttrId(R.attr.superBottomSheet_animateCornerRadius)

        if (boolId == INVALID_RESOURCE_ID) {
            return context!!.resources.getBoolean(R.bool.super_bottom_sheet_animate_corner_radius)
        }

        return resources.getBoolean(boolId)
    }

    //endregion
}