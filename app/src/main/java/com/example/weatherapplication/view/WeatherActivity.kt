package com.example.weatherapplication.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.*
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.weatherapplication.R
import com.example.weatherapplication.util.ManagePermissions
import com.example.weatherapplication.viewmodel.MainViewModel
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.weather_activity2.*
import java.util.*


class WeatherActivity : AppCompatActivity() {

    private lateinit var viewmodel: MainViewModel
    private val TAG = "WeatherActivity"
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var currentLocation: Location? = null
    private val REQUEST_CODE = 123
    private lateinit var managePermissions: ManagePermissions

    var city = ""
    var lat = 0.toDouble()
    var long = 0.toDouble()
    private val permissionsRequestCodeLocation = 500

    private val locationPermissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity2)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var cName = GET.getString("cityName", "")?.toLowerCase()


        getLiveData()

        swipe_refresh_layout.setOnRefreshListener {
            ll_data.visibility = View.GONE
            tv_error.visibility = View.GONE
            pb_loading.visibility = View.GONE

            swipe_refresh_layout.isRefreshing = false

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        Log.d("GetAddressIntentService", "locationResult: $location")
                        println("location latituded : ${location.latitude}.")
                        println("location longitude : ${location.longitude}.")
                    }

                    currentLocation = locationResult.locations[0]
                    println("location : ${locationResult.locations[0]}")

                    val geocoder = Geocoder(this@WeatherActivity, Locale.getDefault())
                    var addressesRefresh: List<Address>? = null
                    addressesRefresh = geocoder.getFromLocation(
                        currentLocation?.latitude!!,
                        currentLocation?.longitude!!,
                        1
                    )
                    city = addressesRefresh[0].subAdminArea.toLowerCase().replace("kota", "").replace(
                        "kabupaten",
                        ""
                    )
                    lat = addressesRefresh[0].latitude
                    long = addressesRefresh[0].longitude
                    SET.putString("ADDRESS_RESULT_LAT", lat.toString())
                    SET.putString("ADDRESS_RESULT_LONG", long.toString())
                    SET.apply()
                    println("city : $city")
                    edt_city_name.setText(city)
                    viewmodel.refreshData(cName!!)
                    val cityName = edt_city_name.text.toString()
                    SET.putString("cityName", cityName)
                    SET.apply()
                    viewmodel.refreshData(cityName)
                }
            }
            startLocationUpdates()
        }

        requestPermissionOnlyOnce(permissionsRequestCodeLocation, locationPermissions)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d("GetAddressIntentService", "locationResult: $location")
                    println("location latituded : ${location.latitude}.")
                    println("location longitude : ${location.longitude}.")
                }
                currentLocation = locationResult.locations[0]
                println("location : ${locationResult.locations[0]}")

                val geocoder = Geocoder(this@WeatherActivity, Locale.getDefault())
                var addresses: List<Address>? = null
                addresses = geocoder.getFromLocation(
                    currentLocation?.latitude!!,
                    currentLocation?.longitude!!,
                    1
                )
                println("addresses : ${addresses[0].subAdminArea}")
                println("addresses : ${addresses[0].latitude}")
                println("addresses : ${addresses[0].longitude}")
                city = addresses[0].subAdminArea.toLowerCase().replace("kota", "").replace(
                    "kabupaten",
                    ""
                )
                lat = addresses[0].latitude
                long = addresses[0].longitude
                SET.putString("ADDRESS_RESULT_LAT", lat.toString())
                SET.putString("ADDRESS_RESULT_LONG", long.toString())
                println("city : $city")
                edt_city_name.setText(city)
                val cityName = edt_city_name.text.toString()
                SET.putString("cityName", cityName)
                SET.apply()
                viewmodel.refreshData(cName!!)
            }
        }

        edt_city_name.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                val cityName = edt_city_name.text.toString()
                SET.putString("cityName", cityName)
                SET.apply()
                viewmodel.refreshData(cityName)
                true
            } else false
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            data.let {
                var cName = GET.getString("cityName", "")?.toLowerCase()
                val lat = (it?.getStringExtra("ADDRESS_RESULT_LAT") ?: "").toDouble()
                val long = (it?.getStringExtra("ADDRESS_RESULT_LONG") ?: "").toDouble()

                val geocoder = Geocoder(this@WeatherActivity, Locale.getDefault())
                var addresses: List<Address>? = null
                addresses = geocoder.getFromLocation(
                    lat,
                    long,
                    1
                )

                city = addresses[0].subAdminArea.toLowerCase().replace("kota", "").replace(
                    "kabupaten",
                    ""
                )
                println("city response $city")
                edt_city_name.setText(city)
                val cityName = edt_city_name.text.toString()
                SET.putString("cityName", cityName)
                SET.apply()
                viewmodel.refreshData(cityName)
            }
        }
    }

    private fun getLiveData() {

        viewmodel.weather_data.observe(this, Observer { data ->
            data?.let {

                ll_data.visibility = View.VISIBLE

                tv_city_code.text = data.sys.country.toString()
                tv_city_name.text = data.name.toString()

                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(img_weather_pictures)

                tv_degree.text = data.main.temp.toString() + "°C"

                tv_humidity.text = data.main.humidity.toString() + "%"
                tv_wind_speed.text = data.wind.speed.toString()
                tv_lat.text = data.coord.lat.toString()
                tv_lon.text = data.coord.lon.toString()

            }
        })

        viewmodel.weather_error.observe(this, Observer { error ->
            error?.let {
                if (error) {
                    tv_error.visibility = View.VISIBLE
                    pb_loading.visibility = View.GONE
                    ll_data.visibility = View.GONE
                } else {
                    tv_error.visibility = View.GONE
                }
            }
        })

        viewmodel.weather_loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    pb_loading.visibility = View.VISIBLE
                    tv_error.visibility = View.GONE
                    ll_data.visibility = View.GONE
                } else {
                    pb_loading.visibility = View.GONE
                }
            }
        })

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            val locationRequest = LocationRequest()
//            val locationGPS: Location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            println("locationGPS : $locationGPS")
//            println("locationGPS longitude : ${locationGPS.longitude}")
//            println("locationGPS latitude : ${locationGPS.latitude}")
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 0
            locationRequest.fastestInterval = 0
            locationRequest.numUpdates = 1

            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Need location service")
            builder.setMessage("Location service are required to do the task.")
            builder.setPositiveButton("Go To Setting") { dialog, which ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            builder.setNeutralButton("Cancel", null)
            builder.create()
            builder.show()
        }
    }

    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionsRequestCodeLocation -> {
                val isPermissionsGranted = managePermissions.processPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
                )
                if (isPermissionsGranted) {
                    startLocationUpdates()
                } else {
                    Toast.makeText(applicationContext, "Permissions denied.", Toast.LENGTH_LONG)
                }
                return
            }
        }
    }

    fun requestPermissionOnlyOnce(codePermission: Int, list: List<String>) {
        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this, list, codePermission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            managePermissions.checkPermissionsOnlyOnce()
        } else {
            when (codePermission) {
                permissionsRequestCodeLocation -> {
                    startLocationUpdates()
                }
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}