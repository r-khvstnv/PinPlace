/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.ui.placedetails

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rkhvstnv.pinplace.R
import com.rkhvstnv.pinplace.databinding.FragmentPlaceDetailsBinding
import com.rkhvstnv.pinplace.ui.BaseFragment
import com.rkhvstnv.pinplace.utils.PlaceUtils
import com.rkhvstnv.pinplace.utils.appComponent
import com.rkhvstnv.pinplace.utils.loadImage
import javax.inject.Inject



class PlaceDetailsFragment : BaseFragment(), OnMapReadyCallback {
    private var _binding: FragmentPlaceDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: PlaceDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<PlaceDetailsViewModel> {
        vmFactory
    }

    private val MAP_BUNDLE_KEY = "MapViewBundleKey"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.placeOnMap.apply {
            onCreate(savedInstanceState?.getBundle(MAP_BUNDLE_KEY))
        }

        args.placeId.let {
            id ->
            if (id != -5){
                viewModel.requestPlace(id = id)
            }
        }

        viewModel.place.observe(viewLifecycleOwner){
            p ->
            p?.let {
                with(binding){
                    ivPlaceDetailsImage.loadImage(it.imageSource)
                    tvPlaceDate.text = PlaceUtils.formatToDate(it.date)
                    tvPlaceTitle.text = it.title
                    tvPlaceLocation.text = it.locationName
                    tvPlaceDescription.text = it.description
                }
            }
        }

        /*Method responsible for displaying map or image*/
        viewModel.isMapVisible.observe(viewLifecycleOwner){
            isVisible ->
            with(binding){
                if (isVisible){
                    ivPlaceDetailsImage.visibility = View.GONE
                    placeOnMap.visibility = View.VISIBLE
                    fabMap.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_image_24))
                } else{
                    ivPlaceDetailsImage.visibility = View.VISIBLE
                    placeOnMap.visibility = View.GONE
                    fabMap.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_place_24))
                }
            }
        }

        viewModel.isDeleted.observe(viewLifecycleOwner){
            success ->
            if (success){
                showSnackMessage(getString(R.string.result_deleted))
                findNavController().navigateUp()
            }
        }

        binding.btnDelete.setOnClickListener {
            showDeleteAlertDialog()
        }

        binding.fabMap.setOnClickListener {
            viewModel.flipMapVisibilityState()
        }
    }


    /**Method handles alertDialog for place deleting*/
    private fun showDeleteAlertDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.option_delete)
            .setMessage(R.string.warning_are_you_sure_to_delete)
            .setPositiveButton(R.string.option_delete){ dialog, _ ->
                viewModel.requestPlaceDeleting()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.option_cancel){ dialog, _ ->
                dialog.cancel()
            }
        builder.create().show()
    }

    override fun onStart() {
        super.onStart()
        binding.placeOnMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.placeOnMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.placeOnMap.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapBundle: Bundle? = outState.getBundle(MAP_BUNDLE_KEY)
        if (mapBundle == null) {
            mapBundle = Bundle()
            outState.putBundle(MAP_BUNDLE_KEY, mapBundle)
        }

        binding.placeOnMap.onSaveInstanceState(mapBundle)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.placeOnMap.onLowMemory()
    }

    override fun onDestroyView() {
        binding.placeOnMap.getMapAsync { it.clear() }
        binding.placeOnMap.onDestroy()
        _binding = null
        super.onDestroyView()
    }

    override fun onMapReady(gm: GoogleMap) {
        viewModel.place.observe(viewLifecycleOwner){
                p ->
            p?.let {
                val latLng = LatLng(it.lat, it.lon)
                gm.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(218F))
                )
                val latLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                gm.animateCamera(latLngZoom)
            }
        }
    }

}