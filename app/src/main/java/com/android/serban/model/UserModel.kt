package com.android.serban.model

class UserModel {
    private var name: String? = null
    private var username: String? = null
    private var email: String? = null
    private var password: String? = null
    private var phone: String? = null
    private var img: String? = null

    constructor() {}
    constructor(name: String, username: String, email: String, password: String, phone: String, img : String) {
        this.name = name
        this.username = username
        this.email = email
        this.password = password
        this.phone = phone
        this.img = img
    }
    fun getName() : String{return  name!!}
    fun getUsername() : String{return  username!!}
    fun getEmail() : String{return email!!}
    fun getPassword() : String{return password!!}
    fun getPhone() : String{return phone!!}
    fun getImg() : String{return img!!}

    fun setName(name : String){this.name = name}
    fun setUsername(username : String){this.username = username}
    fun setEmail(email: String){this.email = email}
    fun setPassword(password: String){this.password = password}
    fun setPhone(phone: String) {this.phone = phone}
    fun setImg(img: String) {this.img = img}
}