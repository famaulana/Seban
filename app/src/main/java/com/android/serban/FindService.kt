package com.android.serban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.find_service_tambal.*

class FindService : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_service_tambal)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn_cancel.setOnClickListener {
            val intent = Intent(this@FindService, MapsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        val latLngOrigin = LatLng(-6.1648221, 106.7651024)
        val latLngDestination = LatLng(-6.1648628, 106.7648046)
        this.googleMap!!.addMarker(MarkerOptions().position(latLngOrigin).title("UR Location"))
        this.googleMap!!.addMarker(MarkerOptions().position(latLngDestination).title("Bengkel"))
        this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 18.5f))

    }
}

