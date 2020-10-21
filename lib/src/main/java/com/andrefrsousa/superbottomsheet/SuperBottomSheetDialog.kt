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
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.andrefrsousa.superbottomsheet.databinding.SuperBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.State

internal class SuperBottomSheetDialog : AppCompatDialog {

    private lateinit var binding: SuperBottomSheetDialogBinding
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    internal var cancelable = true
    private var canceledOnTouchOutside = true
    private var canceledOnTouchOutsideSet = false

    private val bottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, @State newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) cancel()
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Do nothing.
        }
    }

    // region Constructor

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
        this.cancelable = cancelable
    }

    // endregion

    // region Methods from AppCompatDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.runIfNotNull {
            if (hasMinimumSdk(Build.VERSION_CODES.LOLLIPOP)) {
                @Suppress("DEPRECATION")
                addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }

            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun setContentView(@LayoutRes layoutResId: Int) = super.setContentView(wrapInBottomSheet(layoutResId, null, null))

    override fun setContentView(view: View) = super.setContentView(wrapInBottomSheet(0, view, null))

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) = super.setContentView(wrapInBottomSheet(0, view, params))

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)

        if (this.cancelable != cancelable) {
            this.cancelable = cancelable

            if (::behavior.isInitialized) {
                behavior.isHideable = cancelable
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (::behavior.isInitialized) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.addBottomSheetCallback(bottomSheetCallback)
        }
    }

    override fun onStop() {
        behavior.removeBottomSheetCallback(bottomSheetCallback)
        super.onStop()
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)

        if (cancel && !cancelable) cancelable = true
        canceledOnTouchOutside = cancel
        canceledOnTouchOutsideSet = true
    }

    // endregion

    // region Private methods

    @SuppressLint("ClickableViewAccessibility")
    private fun wrapInBottomSheet(layoutResId: Int, view: View?, params: ViewGroup.LayoutParams?): View {
        var supportView = view

        binding = SuperBottomSheetDialogBinding.inflate(layoutInflater)

        if (layoutResId != 0 && supportView == null) {
            supportView = layoutInflater.inflate(layoutResId, binding.coordinator, false)
        }

        behavior = BottomSheetBehavior.from(binding.superBottomSheet)
        behavior.isHideable = cancelable

        if (params == null) {
            binding.superBottomSheet.addView(supportView)

        } else binding.superBottomSheet.addView(supportView, params)

        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        binding.touchOutside.setOnClickListener {
            if (cancelable && isShowing && shouldWindowCloseOnTouchOutside()) {
                cancel()
            }
        }

        // Handle accessibility events
        ViewCompat.setAccessibilityDelegate(binding.superBottomSheet, object : AccessibilityDelegateCompat() {

            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)

                when {
                    cancelable -> {
                        info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS)
                        info.isDismissable = true
                    }

                    else -> info.isDismissable = false
                }
            }

            override fun performAccessibilityAction(host: View, action: Int, args: Bundle): Boolean {
                if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && cancelable) {
                    cancel()
                    return true
                }

                return super.performAccessibilityAction(host, action, args)
            }
        })

        binding.superBottomSheet.setOnTouchListener { _, _ ->
            // Consume the event and prevent it from falling through
            true
        }

        return binding.container
    }

    private fun shouldWindowCloseOnTouchOutside(): Boolean {
        if (!canceledOnTouchOutsideSet) {
            if (hasMinimumSdk(Build.VERSION_CODES.HONEYCOMB)) {
                canceledOnTouchOutside = true

            } else {
                val typedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.windowCloseOnTouchOutside))
                canceledOnTouchOutside = typedArray.getBoolean(0, true)
                typedArray.recycle()
            }

            canceledOnTouchOutsideSet = true
        }

        return canceledOnTouchOutside
    }

    // endregion
}