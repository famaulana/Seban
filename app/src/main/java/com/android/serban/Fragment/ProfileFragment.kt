package com.android.serban.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.serban.EditProfile
import com.android.serban.Login
import com.android.serban.PrefHelper
import com.android.serban.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.profile.*
import kotlinx.android.synthetic.main.profile_edit.*

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile, container, false)
    }
    lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var preferences: PrefHelper
    lateinit var storageReference: StorageReference
    lateinit var firebaseStorage: FirebaseStorage
  
    lateinit var filePathImage: Uri
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = PrefHelper(context!!)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("username").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tv_name.setText(p0.value.toString())
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
                        .into(ava1)
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
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