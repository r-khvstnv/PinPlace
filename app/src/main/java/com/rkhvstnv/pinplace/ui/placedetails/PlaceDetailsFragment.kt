package com.rkhvstnv.pinplace.ui.placedetails

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
            getMapAsync(this@PlaceDetailsFragment)
            onCreate(savedInstanceState)
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

        viewModel.isMapVisible.observe(viewLifecycleOwner){
            isVisible ->
            with(binding){
                if (isVisible){
                    ivPlaceDetailsImage.visibility = View.GONE
                    fabMap.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_image_24))
                } else{
                    ivPlaceDetailsImage.visibility = View.VISIBLE
                    fabMap.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_place_24))
                }
            }
        }

        viewModel.isDeleted.observe(viewLifecycleOwner){
            success ->
            if (success){
                findNavController().popBackStack()
            }
        }

        binding.btnDelete.setOnClickListener {
            viewModel.requestPlaceDeleting()
        }

        binding.fabMap.setOnClickListener {
            viewModel.flipMapVisibilityState()
        }
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
        binding.placeOnMap.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.placeOnMap.onLowMemory()
    }

    override fun onDestroyView() {
        binding.placeOnMap.onDestroy()
        _binding = null
        super.onDestroyView()
    }

    override fun onMapReady(gm: GoogleMap) {
        viewModel.place.observe(viewLifecycleOwner){
                p ->
            p?.let {
                val latLng = LatLng(it.lat, it.lon)
                gm.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(218F)))
                val latLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                gm.animateCamera(latLngZoom)
            }
        }
    }

}