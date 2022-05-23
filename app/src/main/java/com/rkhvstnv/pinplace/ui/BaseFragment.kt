package com.rkhvstnv.pinplace.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.rkhvstnv.pinplace.R

open class BaseFragment: Fragment() {

    fun showSnackMessage(text: String){
        val sb = Snackbar.make(
            activity?.findViewById(android.R.id.content)!!,
            text,
            Snackbar.LENGTH_LONG)
        sb.setAnchorView(R.id.nav_view)
        sb.show()
    }

    /**Method checks that GPS and Network functionality is enabled*/
    fun isLocationFunctionalityEnabled(): Boolean{
        //access to the system location services
        val locationManager: LocationManager =
            ContextCompat.getSystemService(requireContext(), LocationManager::class.java)!!
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**Method requests location setting for current app*/
    fun requestLocationSettings(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    /**Method requests current location data and on success request weather update.
     * Inside the method is implemented selfPermission checking */
    @SuppressLint("MissingPermission")
    fun currentLocationClient(listener: LocationListener){
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val locationRequest = LocationRequest
            .create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation.let {
                    listener.onLocationResult(result = result)
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
        fusedLocationClient
            .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
    }

    interface LocationListener{
        fun onLocationResult(result: LocationResult)
    }
}