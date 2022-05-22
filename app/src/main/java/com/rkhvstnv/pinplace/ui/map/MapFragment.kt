package com.rkhvstnv.pinplace.ui.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.databinding.BottomSheetPlaceDetailsBinding
import com.rkhvstnv.pinplace.databinding.FragmentMapBinding
import com.rkhvstnv.pinplace.ui.BaseFragment
import com.rkhvstnv.pinplace.utils.appComponent
import com.rkhvstnv.pinplace.utils.loadImage
import javax.inject.Inject

class MapFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val viewModel: MapViewModel by viewModels {
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
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapViewAll.apply {
            getMapAsync(this@MapFragment)
            onCreate(savedInstanceState)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapViewAll.onStart()
    }

    override fun onMapReady(gm: GoogleMap) {
        val bounds = LatLngBounds.Builder()
        viewModel.allPlacesList.observe(viewLifecycleOwner){
            list ->
            list?.let {
                for (place in list){
                    val latLng = LatLng(place.lat, place.lon)
                    val marker = gm.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(218F)))
                    marker.tag = place

                    bounds.include(latLng)
                }

                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 25, 25, 5)
                gm.animateCamera(cameraUpdate)
            }
        }
        gm.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return if (marker != null){
            showBottomSheetPlace(marker.tag as PlaceEntity)
            true
        } else
            false

    }


    private fun showBottomSheetPlace(place: PlaceEntity){
        val dialog = BottomSheetDialog(requireContext())
        val dBinding = BottomSheetPlaceDetailsBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dBinding.root)

        with(dBinding){
            ivImageBottom.loadImage(place.imageSource)
            tvTitleBottom.text = place.title
            tvLocationBottom.text = place.locationName
        }

        dialog.show()
    }


    override fun onResume() {
        super.onResume()
        binding.mapViewAll.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapViewAll.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapViewAll.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapViewAll.onLowMemory()
    }

    override fun onDestroyView() {
        binding.mapViewAll.onDestroy()
        _binding = null
        super.onDestroyView()
    }
}