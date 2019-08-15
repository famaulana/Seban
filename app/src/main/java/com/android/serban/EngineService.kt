package com.android.serban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.engine_service.*


class EngineService : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.engine_service)

        btn_order.setOnClickListener {
            startActivity(Intent(this, LoadingActivity::class.java))
        }


    }
}