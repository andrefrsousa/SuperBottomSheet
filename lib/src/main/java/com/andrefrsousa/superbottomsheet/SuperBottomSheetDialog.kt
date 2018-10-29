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
import android.content.Context
import android.content.DialogInterface.OnCancelListener
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
import android.support.design.widget.BottomSheetBehavior.State
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.AccessibilityDelegateCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.support.v7.app.AppCompatDialog
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout

internal class SuperBottomSheetDialog : AppCompatDialog {

    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    private var mCancelable = true
    private var mCanceledOnTouchOutside = true
    private var mCanceledOnTouchOutsideSet: Boolean = false

    @Suppress("unused")
    constructor(context: Context?) : this(context, 0)

    constructor(context: Context?, theme: Int) : super(context, theme) {
        // We hide the title bar for any style configuration. Otherwise, there will be a gap
        // above the bottom sheet when it is expanded.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    @Suppress("unused")
    constructor(context: Context?, cancelable: Boolean, cancelListener: OnCancelListener?) : super(context, cancelable, cancelListener) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        mCancelable = cancelable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.runIfNotNull {
            if (hasMinimumSdk(Build.VERSION_CODES.LOLLIPOP)) {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }

            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun setContentView(@LayoutRes layoutResId: Int)  = super.setContentView(wrapInBottomSheet(layoutResId, null, null))

    override fun setContentView(view: View) = super.setContentView(wrapInBottomSheet(0, view, null))

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) = super.setContentView(wrapInBottomSheet(0, view, params))

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)

        if (mCancelable != cancelable) {
            mCancelable = cancelable

            if (::behavior.isInitialized) {
                behavior.isHideable = cancelable
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (::behavior.isInitialized) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)

        if (cancel && !mCancelable) {
            mCancelable = true
        }

        mCanceledOnTouchOutside = cancel
        mCanceledOnTouchOutsideSet = true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun wrapInBottomSheet(layoutResId: Int, view: View?, params: ViewGroup.LayoutParams?): View {
        var supportView = view

        val container = View.inflate(context, R.layout.super_bottom_sheet_dialog, null)
        val coordinator = container.findViewById<CoordinatorLayout>(R.id.coordinator)

        if (layoutResId != 0 && supportView == null) {
            supportView = layoutInflater.inflate(layoutResId, coordinator, false)
        }

        val bottomSheet = coordinator.findViewById<FrameLayout>(R.id.super_bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.setBottomSheetCallback(mBottomSheetCallback)
        behavior.isHideable = mCancelable

        if (params == null) {
            bottomSheet.addView(supportView)

        } else {
            bottomSheet.addView(supportView, params)
        }

        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        coordinator.findViewById<View>(R.id.touch_outside).setOnClickListener {
            if (mCancelable && isShowing && shouldWindowCloseOnTouchOutside()) {
                cancel()
            }
        }

        // Handle accessibility events
        ViewCompat.setAccessibilityDelegate(bottomSheet, object : AccessibilityDelegateCompat() {

            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)

                when {
                    mCancelable -> {
                        info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS)
                        info.isDismissable = true
                    }

                    else -> info.isDismissable = false
                }
            }

            override fun performAccessibilityAction(host: View, action: Int, args: Bundle): Boolean {
                if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && mCancelable) {
                    cancel()
                    return true
                }

                return super.performAccessibilityAction(host, action, args)
            }
        })

        bottomSheet.setOnTouchListener { _, _ ->
            // Consume the event and prevent it from falling through
            true
        }
        return container
    }

    private fun shouldWindowCloseOnTouchOutside(): Boolean {
        if (!mCanceledOnTouchOutsideSet) {
            if (hasMinimumSdk(Build.VERSION_CODES.HONEYCOMB)) {
                mCanceledOnTouchOutside = true

            } else {
                val typedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.windowCloseOnTouchOutside))
                mCanceledOnTouchOutside = typedArray.getBoolean(0, true)
                typedArray.recycle()
            }

            mCanceledOnTouchOutsideSet = true
        }

        return mCanceledOnTouchOutside
    }

    private val mBottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, @State newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                cancel()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
}