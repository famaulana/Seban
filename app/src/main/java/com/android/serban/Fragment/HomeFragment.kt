package com.android.serban.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.serban.EngineService
import com.android.serban.MapsActivity
import com.android.serban.PrefHelper
import com.android.serban.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.profile.*

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home, container, false)
    }
    lateinit var fAuth: FirebaseAuth
    lateinit var preferences: PrefHelper
    lateinit var storageReference: StorageReference
    lateinit var firebaseStorage: FirebaseStorage
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = PrefHelper(context!!)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("username").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tv_name2.setText(p0.value.toString())
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("img").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    Glide.with(context!!).load(p0.value.toString())
                        .centerCrop()
                        .error(R.drawable.ic_launcher_background)
                        .into(ava2)
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })



        ln_home_serviceTambal.setOnClickListener {
            startActivity(Intent(activity!!, MapsActivity::class.java))

        }
        ll_service_engine_home.setOnClickListener {
            startActivity(Intent(activity!!, EngineService::class.java))

        }

    }

    companion object {
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

    }
}