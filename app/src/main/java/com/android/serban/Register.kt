package com.android.serban

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.signup.*

class Register : AppCompatActivity(){
    lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        fAuth = FirebaseAuth.getInstance()

        btn_signup.setOnClickListener {
            val username = et_username2.text.toString()
            val email = et_email.text.toString()
            val password = et_password2.text.toString()
            val telp = et_phone.text.toString()
            if (email.isNotEmpty() || password.isNotEmpty() || username.isNotEmpty() || telp.isNotEmpty() || !email.equals("")
                || !password.equals("") || !username.equals("")
                || !telp.equals("")
            ) {
                fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this, "Registration Done!",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBackPressed()
                        } else {
                            Toast.makeText(
                                this, "Failed To Registration",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this, "Data must be input",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }
}