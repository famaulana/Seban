package com.android.serban

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.serban.Common.Common
import com.android.serban.Remote.IGoogleService
import com.android.serban.model.MyPlaces
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.maps_seban_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap: GoogleMap

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker? = null

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    lateinit var mService: IGoogleService

    internal lateinit var currentPlace: MyPlaces


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_seban_activity)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //init service
        mService = Common.googleApiService


                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_bike -> nearByPlace("bengkel")
                R.id.action_car -> nearByPlace("car_repair")
            }
            true
        }
    }

    private fun nearByPlace(typePlace: String) {

        mMap.clear()
        val url = getUrl(latitude, longitude, typePlace)

        mService.getNearbyPlaces(url)
            .enqueue(object : Callback<MyPlaces> {
                override fun onResponse(call: Call<MyPlaces>, response: Response<MyPlaces>) {
                    currentPlace = response.body()!!
                    if (response.isSuccessful) {

                        for (i in 0 until response.body()!!.results!!.size) {
                            val markerOptions = MarkerOptions()
                            val googlePlaces = response.body()!!.results!![i]
                            val lat = googlePlaces.geometry!!.location!!.lat
                            val lng = googlePlaces.geometry!!.location!!.lng
                            val placeName = googlePlaces.name
                            val latLng = LatLng(lat, lng)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)

                            if (typePlace.equals("bengkel"))
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
                            else if (typePlace.equals("car_repair"))
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mobil))
                            else
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                            markerOptions.snippet(i.toString())

                            mMap.addMarker(markerOptions)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f))
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))


                        }


                    }
                }

                override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
                    Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun getUrl(latitude: Double, longitude: Double, typePlace: String): String {

        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=$latitude,$longitude")
        googlePlaceUrl.append("&radius=500")
        googlePlaceUrl.append("&type=$typePlace")
        googlePlaceUrl.append("&key=AIzaSyDqdjnLBn8xJWo4qZh7JYRDQzte-pED_9g")

        Log.d("URL_DEBUG", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0.locations.size - 1)

                if (mMarker != null) {
                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mMarker = mMap.addMarker(markerOptions)

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            return false
        } else
            return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        if (checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallback()

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                            )
                            mMap.isMyLocationEnabled = true
                        }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
            }
        } else
            mMap.isMyLocationEnabled = true

        mMap.uiSettings.isZoomControlsEnabled = true
//        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(LatLng(-6.1977165,106.7304912), 14.0f) )
    }
}