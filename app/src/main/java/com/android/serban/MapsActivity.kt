package com.android.serban

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.maps_seban_activity.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false

    private val buttonState = 1
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    companion object {
        private const val LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_seban_activity)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        findService.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        bikeSelect.setOnClickListener {
            if (buttonState % 2 == 0) {
                bikeSelect.setBackgroundColor(resources.getColor(R.color.white))
                carSelect.setBackgroundColor(resources.getColor(R.color.colorPrimary))

            } else {
                bikeSelect.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                carSelect.setBackgroundColor(resources.getColor(R.color.white))
            }
        }
        carSelect.setOnClickListener {
            if (buttonState % 2 == 0) {
                bikeSelect.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                carSelect.setBackgroundColor(resources.getColor(R.color.white))

            } else {
                bikeSelect.setBackgroundColor(resources.getColor(R.color.white))
                carSelect.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
        }
        findService.setOnClickListener {
            val intent = Intent(this@MapsActivity, LoadingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        settingUpMaps()
//         Add a marker in Sydney and move the camera
        val sarkom = LatLng(-6.1647626, 106.7627832)
        mMap.addMarker(MarkerOptions().position(sarkom).title("Sarkom Hoho"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sarkom))
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        val markerOptions = MarkerOptions().position(sarkom)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round)
            )
        )
        mMap.addMarker(markerOptions.title("Sarkom"))
    }

    private fun settingUpMaps() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION
            )
            return
        }
        mMap.isBuildingsEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentPost = LatLng(location.latitude, location.longitude)
                placeMarkerInMaps(currentPost)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPost, 18.0f))
            }
        }
    }

    //
    fun placeMarkerInMaps(loc: LatLng) {
        val markerOptions = MarkerOptions().position(loc)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            )
        )
        mMap.addMarker(markerOptions)


    }
}