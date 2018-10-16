/*
 * Copyright (c) 2018 André Sousa.
 */
package com.andrefrsousa.superbottomsheet.demo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show_sheet.setOnClickListener {
            val sheet = DemoBottomSheetFragment()
            sheet.show(supportFragmentManager, sheet.tag)
        }
    }
}

class DemoBottomSheetFragment : SuperBottomSheetFragment() {

    override fun getInnerFragment() = DemoInnerFragment.newInstance()

    override fun getInnerFragmentTag() = "DemoInnerFragment"

}

class DemoInnerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_demo_inner, container, false)
    }

    // Constructor
    companion object {
        internal fun newInstance() = DemoInnerFragment()
    }
}