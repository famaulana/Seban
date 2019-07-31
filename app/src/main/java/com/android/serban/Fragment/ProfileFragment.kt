package com.android.serban.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.serban.EditProfile
import com.android.serban.Login
import com.android.serban.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.profile.*

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile, container, false)
    }
    lateinit var fAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lyt_edit_profile.setOnClickListener {
            startActivity(Intent(activity!!, EditProfile::class.java))
        }
        fAuth = FirebaseAuth.getInstance()
        lyt_logout.setOnClickListener {
            fAuth.signOut()
            val intent = Intent(context, Login::class.java)
            startActivity(intent)
            activity!!.finish()
        }
    }

    companion object {
        fun newInstance(): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

    }

}