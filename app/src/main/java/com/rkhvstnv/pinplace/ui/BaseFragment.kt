package com.rkhvstnv.pinplace.ui

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.rkhvstnv.pinplace.R

open class BaseFragment: Fragment() {

    fun showSnackMessage(text: String){
        val sb = Snackbar.make(
            activity?.findViewById(android.R.id.content)!!,
            text,
            Snackbar.LENGTH_LONG)
        sb.setAnchorView(R.id.nav_view)
        sb.show()
    }
}