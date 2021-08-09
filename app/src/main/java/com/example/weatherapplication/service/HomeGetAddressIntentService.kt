package com.example.weatherapplication.service

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import java.util.*

class HomeGetAddressIntentService : IntentService("GetAddressIntentService") {

    private var addressResultReceiver: ResultReceiver? = null
    private var location: Location? = null

    override fun onHandleIntent(intent: Intent?) {
        //get result receiver from intent
        addressResultReceiver = intent?.getParcelableExtra("ADD_RECEIVER")

        if (addressResultReceiver == null) {
            Log.e("GetAddressIntentService", "No receiver, not processing the request further")
            return
        }

        location = intent?.getParcelableExtra("ADD_LOCATION")

        //send no location error to results receiver
        if (location == null) {
            sendResultsToReceiver(0, "No location, can't go further without location", null)
            return
        }

        //call GeoCoder getFromLocation to get address
        //returns list of addresses, take first one and send info to result receiver
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null

        try {
            addresses = geocoder.getFromLocation(
                location?.latitude!!,
                location?.longitude!!,
                1
            )
        } catch (ioException: Exception) {
            Log.e("GetAddressIntentService", "Error in getting address for the location")
        }

        if (addresses == null || addresses.isEmpty()) {
            sendResultsToReceiver(1, "No address found for the location", null)
        } else {
            sendResultsToReceiver(2, "Address found", addresses[0])
        }
    }

    //to send results to receiver in the source activity
    private fun sendResultsToReceiver(resultCode: Int, message: String, address: Address?) {
        val bundle = Bundle()
        bundle.putString("ADDRESS_RESULT", message)
        bundle.putDouble("ADDRESS_RESULT_LAT", address?.latitude ?: 0.0)
        bundle.putDouble("ADDRESS_RESULT_LONG", address?.longitude ?: 0.0)
        bundle.putString("ADDRESS_RESULT_DISTRICT", address?.locality.toString().replace("Kecamatan ", ""))

        addressResultReceiver?.send(resultCode, bundle)
    }
}