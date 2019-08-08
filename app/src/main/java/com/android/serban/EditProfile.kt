package com.android.serban

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.profile_edit.*
import java.io.IOException

class EditProfile : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    lateinit var preferences: PrefHelper
    lateinit var storageReference: StorageReference
    lateinit var firebaseStorage: FirebaseStorage
    val REQUEST_CODE_IMAGE = 10002
    val PERMISSION_RC = 10003
    var value = 0.0
    lateinit var filePathImage: Uri
    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            REQUEST_CODE_IMAGE
        )
    }
    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_CODE_IMAGE -> {
                filePathImage = data?.data!!
                try {
                    val bitmap: Bitmap = MediaStore
                        .Images.Media.getBitmap(
                        this.contentResolver, filePathImage
                    )
                    Glide.with(this).load(bitmap)
                        .override(250, 250)
                        .centerCrop().into(ava)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit)
        preferences = PrefHelper(this)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        ava.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), PERMISSION_RC
                        )
                    } else {
                        imageChooser()
                    }
                }
                else -> {
                    imageChooser()
                }
            }
        }
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("img").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    Glide.with(this@EditProfile).load(p0.value.toString())
                        .centerCrop()
                        .error(R.drawable.ic_launcher_background)
                        .into(ava)
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("username").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tv_edit_username.setText(p0.value.toString())
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    et_edit_name.setText(p0.value.toString())
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("phone").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    et_edit_number.setText(p0.value.toString())
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("email").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    et_edit_email.setText(p0.value.toString())
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("password").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    et_edit_password.setText(p0.value.toString())
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        btn_save.setOnClickListener {
            val uidUser = fAuth.currentUser?.uid
            val counter = preferences.getUID()
            dbRef = FirebaseDatabase.getInstance().reference
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e("Error", p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val editno = et_edit_number.text.toString()
                    val editname = et_edit_name.text.toString()
                    val editpass = et_edit_password.text.toString()
                    val editmail = et_edit_email.text.toString()
                    try {
                        val storageRef: StorageReference = storageReference
                            .child("img/$uidUser/${preferences.getUID()}.${GetFileExtension(filePathImage)}")
                        storageRef.putFile(filePathImage).addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener {
                                dbRef.child("user/$uidUser/img").setValue(it.toString())
                            }
                        }.addOnFailureListener {
                            Log.e("TAG_ERROR", it.message)
                        }.addOnProgressListener { taskSnapshot ->
                            value = (100.0 * taskSnapshot
                                .bytesTransferred / taskSnapshot.totalByteCount)
                        }
                    } catch (e: UninitializedPropertyAccessException) {
                        Toast.makeText(this@EditProfile, "Sukses", Toast.LENGTH_SHORT).show()
                    }
                    dbRef.child("user/$uidUser/password").setValue(editpass)
                    dbRef.child("user/$uidUser/email").setValue(editmail)
                    dbRef.child("user/$uidUser/name").setValue(editname)
                    dbRef.child("user/$uidUser/phone").setValue(editno)
                    Toast.makeText(this@EditProfile, "Sukses", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}