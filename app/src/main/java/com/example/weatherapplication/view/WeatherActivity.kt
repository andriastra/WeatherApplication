package com.example.weatherapplication.view

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.R
import com.example.weatherapplication.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.weather_activity2.*
import java.net.URL

class WeatherActivity : AppCompatActivity() {

    private lateinit var viewmodel: MainViewModel

    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity2)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

//        viewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var cName = GET.getString("cityName", "bingöl")?.toLowerCase()
        edt_city_name.setText(cName)
        viewmodel.refreshData(cName!!)

        getLiveData()

        swipe_refresh_layout.setOnRefreshListener {
            ll_data.visibility = View.GONE
            tv_error.visibility = View.GONE
            pb_loading.visibility = View.GONE

            var cityName = GET.getString("cityName", cName)?.toLowerCase()
            edt_city_name.setText(cityName)
            viewmodel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing = false
        }

        img_search_city.setOnClickListener {
            val cityName = edt_city_name.text.toString()
            SET.putString("cityName", cityName)
            SET.apply()
            viewmodel.refreshData(cityName)
            getLiveData()
//            Log.i(TAG, "onCreate: " + cityName)
        }

    }

    private fun getLiveData() {

        viewmodel.weather_data.observe(this, Observer { data ->
            data?.let {
//                ll_data.visibility = View.VISIBLE
//
//                tv_city_code.text = data.sys.country.toString()
//                tv_city_name.text = data.name.toString()
//
//                Glide.with(this)
//                    .load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
//                    .into(img_weather_pictures)
//
//                tv_degree.text = data.main.temp.toString() + "°C"
//
//                tv_humidity.text = data.main.humidity.toString() + "%"
//                tv_wind_speed.text = data.wind.speed.toString()
//                tv_lat.text = data.coord.lat.toString()
//                tv_lon.text = data.coord.lon.toString()

            }
        })

        viewmodel.weather_error.observe(this, Observer { error ->
            error?.let {
//                if (error) {
//                    tv_error.visibility = View.VISIBLE
//                    pb_loading.visibility = View.GONE
//                    ll_data.visibility = View.GONE
//                } else {
//                    tv_error.visibility = View.GONE
//                }
            }
        })

        viewmodel.weather_loading.observe(this, Observer { loading ->
            loading?.let {
//                if (loading) {
//                    pb_loading.visibility = View.VISIBLE
//                    tv_error.visibility = View.GONE
//                    ll_data.visibility = View.GONE
//                } else {
//                    pb_loading.visibility = View.GONE
//                }
            }
        })

    }

    fun getLocation() {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        val locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {
                var latitute = location.latitude
                var longitute = location.longitude

                Log.i("test", "Latitute: $latitute ; Longitute: $longitute")

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }
//        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED ->
                    Toast.makeText(applicationContext,"", Toast.LENGTH_LONG)
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}