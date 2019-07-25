package com.android.serban.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.serban.R

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chat, container, false)
    }

    companion object {
        fun newInstance(): ChatFragment {
            val fragment = ChatFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

    }
}