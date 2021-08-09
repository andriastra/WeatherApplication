package com.example.weatherapplication.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherapplication.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val TAG = MapsActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    var city = ""
    var latitude = ""
    var longitude = ""
    var changeLatitude = ""
    var changeLongitude = ""
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor

    var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()
        latitude = GET.getString("ADDRESS_RESULT_LAT", "0")?:"0"
        longitude = GET.getString("ADDRESS_RESULT_LONG", "0")?:"0"
        onSelectedMap()
    }

    private fun onSelectedMap() {
        tv_finish.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.apply {
                putExtra("ADDRESS_RESULT_LAT", changeLatitude)
                putExtra("ADDRESS_RESULT_LONG", changeLongitude)
            }
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //These coordinates represent the lattitude and longitude of the Googleplex.
        println("latitude $latitude")
        println("longitude $longitude")
        val zoomLevel = 1f
        val overlaySize = 100f

        val homeLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))

        changeMap(homeLatLng)
        setPoiClick(map)
        enableMyLocation()
    }

    // Initializes contents of Activity's standard options menu. Only called the first time options
    // menu is displayed.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    // Called whenever an item in your options menu is selected.
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun changeMap(latLng: LatLng) {
        map.apply {
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            setOnCameraIdleListener {
                map.apply {
                    // Get full address from lat long
                    val latLongCenter = map.cameraPosition.target
                    println("latLongCenter : $latLongCenter")

                    changeLatitude = latLongCenter.latitude.toString()
                    changeLongitude = latLongCenter.longitude.toString()
                    val geoCoder = Geocoder(this@MapsActivity)
                    try {
                        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        val addresses: List<Address>? =
                            geoCoder.getFromLocation(latLongCenter.latitude, latLongCenter.longitude, 1)
                        var district = ""
                        var postalCode = ""
                        if (addresses != null && addresses.isNotEmpty()) {

                        } else {
                            Log.d(TAG, "geoCoder.getFromLocation empty")
//                                toast(getString(com.google.android.gms.location.R.string.message_address_not_found))
                        }
                    } catch (e: IOException) {
                        // handle exception
                        Log.d(TAG, "geoCoder.getFromLocation empty")
//                            toast(getString(com.google.android.gms.location.R.string.message_address_not_found))
                    }
                }
            }
        }
    }

    // Places a marker on the map and displays an info window that contains POI name.
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    // Checks that users have given permission
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Checks if users have given their location and sets location enabled if so.
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    // Callback for the result from requesting permissions.
    // This method is invoked for every call on requestPermissions(android.app.Activity, String[],
    // int).
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}