/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.ui.addplace

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationResult
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.rkhvstnv.pinplace.R
import com.rkhvstnv.pinplace.databinding.FragmentAddPlaceBinding
import com.rkhvstnv.pinplace.keys.Keys
import com.rkhvstnv.pinplace.ui.BaseFragment
import com.rkhvstnv.pinplace.utils.Constants
import com.rkhvstnv.pinplace.utils.PlaceUtils
import com.rkhvstnv.pinplace.utils.appComponent
import com.rkhvstnv.pinplace.utils.loadImage
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

class AddPlaceFragment : BaseFragment() {
    private var _binding: FragmentAddPlaceBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val viewModel: AddPlaceViewModel by viewModels{
        vmFactory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializePlaceApi()


        with(binding){
            ivImage.setOnClickListener {
                imageSourcePicker()
            }
            etDate.setOnClickListener {
                setDateFromCalendar()
            }
            btnCurrentDate.setOnClickListener {
                setCurrentDate()
            }
            etLocation.setOnClickListener {
                autocompletedPlaceLauncher()
            }
            btnCurrentLocation.setOnClickListener {
                requestCurrentLocationWithPermissionCheck()
            }
            btnAddPlace.setOnClickListener {
                requestPlaceSaving()
            }
        }

        viewModel.isSaved.observe(viewLifecycleOwner){
            success ->
            if (success){
                showSnackMessage(getString(R.string.result_saved_successfully))
                findNavController().popBackStack()
            }
        }

        /*Method helps to display image after configuration changes*/
        viewModel.imagePath.observe(viewLifecycleOwner){
            image ->
            image?.let {
                binding.ivImage.loadImage(it)
                binding.tvAddImage.visibility = View.GONE
            }
        }
    }



    /**Method set current date in millis to mutableLiveDate in viewModel and
     * formatted one to UI*/
    private fun setCurrentDate(){
        val date = System.currentTimeMillis()
        viewModel.setDateL(date)
        binding.etDate.setText(PlaceUtils.formatToDate(date))
    }

    /**Method show Calendar Dialog, where user can set date*/
    private fun setDateFromCalendar(){
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            viewModel.setDateL(calendar.timeInMillis)
            binding.etDate.setText(PlaceUtils.formatToDate(calendar.timeInMillis))
        }

        DatePickerDialog(requireContext(), dateSetListener, calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    /**Fine and Coarse Location Permission Launcher
     * if Granted -> call parent method locationLauncher()*/
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){ permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true){

                locationLauncher()
            } else{
                showSnackMessage(getString(R.string.error_permission_is_not_granted))
            }
        }

    /**Method handles all events corresponding to receiving current location.
     * Firstly it checks that Location functionality (GPS or Network) is enabled:
     * false -> show location settings of app
     * true -> Checks that location permissions are granted:
     *          true -> request current location
     *          false -> request permissions*/
    private fun requestCurrentLocationWithPermissionCheck(){
        if (isLocationFunctionalityEnabled()){
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationLauncher()
            } else{
                locationPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        } else{
            requestLocationSettings()
        }
    }

    /**Method request current location using parent method currentLocationClient() in BaseFragment
     * Using interface LocationListener data will be received from it*/
    private fun locationLauncher() = currentLocationClient(object : LocationListener{
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            location?.let {
                viewModel.setLatLon(location.latitude, location.longitude)
                binding.etLocation.setText(
                    PlaceUtils.getFullAddressFromLocation(
                        requireContext(), location.latitude, location.longitude)
                )
            }
        }
    })


    /**Method show card with gallery & camera options,
     * after click on someone, will hide card and execute corresponding launcher*/
    private fun imageSourcePicker(){
        with(binding){
            mcvImageSource.visibility = View.VISIBLE

            btnGallerySource.setOnClickListener {
                mcvImageSource.visibility = View.GONE
                galleryLauncher()
            }
            btnCameraSource.setOnClickListener {
                mcvImageSource.visibility = View.GONE
                cameraLauncher()
            }
        }
    }


    /**Result handler for storage permission. If permission is granted it calls
     * galleryLauncher()*/
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted){
            galleryLauncher()
        } else{
            showSnackMessage(getString(R.string.error_permission_is_not_granted))
        }
    }

    /**Method request gallery, previously checked that permission for this purpose is granted.
     * Otherwise will request permission*/
    private fun galleryLauncher(){
        when{
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                val galleryIntent = Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryResultLauncher.launch(galleryIntent)
            }
            else -> {
                storagePermissionLauncher
                    .launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    /**Method handles result of galleryLauncher.
     * If RESULT_OK, it will load bitmap to corresponding UI element and
     * saves bitmap in internal storage of app*/
    private val galleryResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    val tmpBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(requireContext().contentResolver, it))
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                    }
                    saveImageToInternalStorage(tmpBitmap)
                }
            }
        }


    /**Result handler for storage permission. If permission is granted it calls
     * cameraLauncher()*/
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted){
            cameraLauncher()
        } else{
            showSnackMessage(getString(R.string.error_permission_is_not_granted))
        }
    }

    /**Method request camera, previously checked that permission for this purpose is granted.
     * Otherwise will request permission*/
    private fun cameraLauncher(){
        when{
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraResultLauncher.launch(cameraIntent)
            }
            else -> {
                cameraPermissionLauncher
                    .launch(Manifest.permission.CAMERA)
            }
        }
    }

    /**Method handles result of cameraLauncher.
     * If RESULT_OK, it will load bitmap to corresponding UI element and
     * saves bitmap in internal storage of app*/
    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if (result.resultCode == Activity.RESULT_OK){
                result.data?.extras?.let {
                    val tmpBitmap = it.get("data") as Bitmap
                    saveImageToInternalStorage(tmpBitmap)
                }
            }
    }


    /**Method saves image to internal package storage and assigns it's imagePath in viewModel*/
    private fun saveImageToInternalStorage(bitmap: Bitmap){
        val wrapper = ContextWrapper(context?.applicationContext)

        var file = wrapper.getDir(Constants.PLACE_IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        runCatching {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }.onFailure {
            it.printStackTrace()
        }
        viewModel.imagePath.value = file.absolutePath
    }


    private fun initializePlaceApi(){
        Places.initialize(requireContext().applicationContext, Keys.PLACES_API_KEY_VALUE)

    }

    private fun autocompletedPlaceLauncher(){
        val fields = listOf(
            Place.Field.ID, Place.Field.NAME,
            Place.Field.LAT_LNG, Place.Field.ADDRESS
        )

        val autocompleteIntent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).build(requireContext())

        autocompleteResultLauncher.launch(autocompleteIntent)
    }

    /**Method handles result of autocompletedPlaceLauncher.
     * If RESULT_OK, it will load address to corresponding UI element and
     * assigns latitude and longitude in viewModel*/
    private val autocompleteResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if (result.resultCode == Activity.RESULT_OK){
                result.data?.let {
                        intent ->
                    val place: Place = Autocomplete.getPlaceFromIntent(intent)
                    place.latLng?.let {
                        viewModel.setLatLon(it.latitude, it.longitude)
                        binding.etLocation.setText(
                            place.address
                        )
                    }
                }
            } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR){
                result.data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    showSnackMessage(status.statusMessage ?: status.statusCode.toString())
                }
            }
    }


    /**Method firstly request error resetting and after
     * will check that fields and imagePath are not empty.
     * Otherwise show error message in corresponding field*/
    private fun isUserInputIsValid(): Boolean{
        var result = false
        val error: String = getString(R.string.error_empty_field)

        resetFieldsErrors()

        with(binding){
            when{
                TextUtils.isEmpty(etTitle.text) -> etTitle.error = error
                TextUtils.isEmpty(etDate.text) -> etDate.error = error
                TextUtils.isEmpty(etLocation.text) -> etLocation.error = error
                TextUtils.isEmpty(etDescription.text) -> etDescription.error = error
                TextUtils.isEmpty(viewModel.imagePath.value) -> imageSourcePicker()
                else -> result = true
            }
        }

        return result
    }

    /**Next method reset errors in all available fields*/
    private fun resetFieldsErrors(){
        with(binding){
            mcvImageSource.visibility = View.GONE
            etTitle.error = null
            etDate.error = null
            etLocation.error = null
            etDescription.error = null
        }
    }


    /**Method firstly checks, that all fields are filled.
     * If true, then request place saving */
    private fun requestPlaceSaving(){
        if (isUserInputIsValid()){
            with(binding){
                viewModel.addPlace(
                    etTitle.text.toString(),
                    etDescription.text.toString(),
                    etLocation.text.toString()
                )
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}