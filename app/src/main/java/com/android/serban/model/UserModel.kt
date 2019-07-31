package com.android.serban.model

class UserModel {
    private var username: String? = null
    private var email: String? = null
    private var password: String? = null
    private var telp: String? = null

    constructor() {}
    constructor(username: String, email: String, password: String, telp: String) {
        this.username = username
        this.email = email
        this.password = password
        this.telp = telp
    }
    fun getUsername() : String{return  username!!}
    fun getEmail() : String{return email!!}
    fun getPassword() : String{return password!!}
    fun getTelp() : String{return telp!!}


    fun setUsername(username : String){this.username = username}
    fun setEmail(email: String){this.email = email}
    fun setPassword(password: String){this.password = password}
    fun setTelp(telp: String) {this.telp = telp}
}