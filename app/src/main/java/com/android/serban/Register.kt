package com.android.serban

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.signup.*

class Register : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference
    lateinit var helperPref: PrefHelper
    lateinit var fAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        fAuth = FirebaseAuth.getInstance()


        btn_signup.setOnClickListener {
            var email = et_email.text.toString()
            var password = et_password2.text.toString()
            var username = et_username2.text.toString()
            var telp = et_phone.text.toString()

            helperPref = PrefHelper(this)
            if (email.isNotEmpty() || password.isNotEmpty() || username.isNotEmpty() || telp.isNotEmpty() ||
                !email.equals("") || !password.equals("") || !username.equals("")  || !telp.equals("")
                || et_email.length() == 6 || et_password2.length() == 6 || et_username2.length() == 6 || et_phone.length() == 6
            ) {
                fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            simpanToFireBase(username, password, telp)
                            Toast.makeText(this, "Register Berhasil!", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        } else {
                            Toast.makeText(this, "Value must be 6 or more digit!", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun simpanToFireBase(username: String, password: String, telp: String) {
        val uidUser = helperPref.getUID()
        val counterId = helperPref.getCounterId()
        dbRef = FirebaseDatabase.getInstance().getReference("user/$uidUser")

        dbRef.child("$counterId/username").setValue(username)
        dbRef.child("$counterId/password").setValue(password)
        dbRef.child("$counterId/telp").setValue(telp)

        Toast.makeText(
            this, "Data Successful added",
            Toast.LENGTH_SHORT
        ).show()
        helperPref.saveCounterId(counterId + 1)
        onBackPressed()
    }
}