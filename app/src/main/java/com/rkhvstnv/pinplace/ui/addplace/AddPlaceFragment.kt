package com.rkhvstnv.pinplace.ui.addplace

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
import com.rkhvstnv.pinplace.utils.loadBitmapImage
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
                currentLocationLauncher()
            }
        }


        viewModel.imagePath.observe(viewLifecycleOwner){
            Log.i("Test", it)
        }
    }


    private fun setCurrentDate(){
        val date = System.currentTimeMillis()
        viewModel.setDateL(date)
        binding.etDate.setText(PlaceUtils.formatToDate(date))
    }

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


    private fun currentLocationLauncher() = requestCurrentLocation(object : LocationListener{
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            viewModel.setLatLon(location.latitude, location.longitude)
            binding.etLocation.setText(
                PlaceUtils.getFullAddressFromLocation(
                    requireContext(), location.latitude, location.longitude)
            )
        }
    })


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

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted){
            galleryLauncher()
        } else{
            showSnackMessage(getString(R.string.error_permission_is_not_granted))
        }
    }

    private fun galleryLauncher(){
        when{
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryResultLauncher.launch(galleryIntent)
            }
            else -> {
                storagePermissionLauncher
                    .launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private val galleryResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    Glide
                        .with(this)
                        .load(it)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("Glide", "Image loading", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                //save image bitmap
                                resource?.let {
                                    saveImageToInternalStorage(resource.toBitmap())
                                }
                                binding.tvAddImage.visibility = View.GONE
                                return false
                            }
                        })
                        .into(binding.ivImage)
                }
            }
        }


    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted){
            cameraLauncher()
        } else{
            showSnackMessage(getString(R.string.error_permission_is_not_granted))
        }
    }

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

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if (result.resultCode == Activity.RESULT_OK){
                result.data?.extras?.let {
                    val tmpBitmap = it.get("data") as Bitmap
                    binding.ivImage.loadBitmapImage(tmpBitmap)
                    binding.tvAddImage.visibility = View.GONE
                    saveImageToInternalStorage(tmpBitmap)
                }
            }
    }


    /**Method saves image to internal package storage and assigns it's imagePath*/
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

        val autocompleteIntent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(requireContext())
        autocompleteResultLauncher.launch(autocompleteIntent)
    }

    private val autocompleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
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
                TextUtils.isEmpty(viewModel.imagePath.value) -> tvAddImage.visibility = View.VISIBLE
                else -> result = true
            }
        }

        return result
    }
    private fun resetFieldsErrors(){
        with(binding){
            tvAddImage.visibility = View.GONE
            tilTitle.isErrorEnabled = false
            tilDate.isErrorEnabled = false
            tilLocation.isErrorEnabled = false
            tilDescription.isErrorEnabled = false
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}