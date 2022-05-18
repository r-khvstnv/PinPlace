package com.rkhvstnv.pinplace.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
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


    //Fine Location Permission launcher
    private fun requestPermissionLauncher(listener: LocationListener) =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->
            if (isGranted){
                getCurrentLocation(listener = listener)
            } else{
                showSnackMessage(getString(R.string.error_permission_is_not_granted))
            }
        }

    /**Method checks that GPS and Network functionality is enabled*/
    private fun isLocationFunctionalityEnabled(): Boolean{
        //access to the system location services
        val locationManager: LocationManager =
            ContextCompat.getSystemService(requireContext(), LocationManager::class.java)!!
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**Method requests location setting for current app*/
    private fun requestLocationSettings(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    /**Method requests current location data and on success request weather update.
     * Inside the method is implemented selfPermission checking */
    private fun getCurrentLocation(listener: LocationListener){
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

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
        } else{
            requestPermissionLauncher(listener = listener)
                .launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun requestCurrentLocation(listener: LocationListener){
        /*Check that GPS functionality is enabled*/
        if (isLocationFunctionalityEnabled()){
            getCurrentLocation(listener = listener)
        } else{
            requestLocationSettings()
        }
    }

    interface LocationListener{
        fun onLocationResult(result: LocationResult)
    }
}