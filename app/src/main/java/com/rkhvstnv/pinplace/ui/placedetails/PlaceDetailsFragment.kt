package com.rkhvstnv.pinplace.ui.placedetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rkhvstnv.pinplace.databinding.FragmentPlaceDetailsBinding
import com.rkhvstnv.pinplace.utils.PlaceUtils
import com.rkhvstnv.pinplace.utils.appComponent
import com.rkhvstnv.pinplace.utils.loadImage
import javax.inject.Inject

class PlaceDetailsFragment : Fragment() {
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
    ): View? {
        _binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.isDeleted.observe(viewLifecycleOwner){
            success ->
            if (success){
                findNavController().popBackStack()
            }
        }

        binding.btnDelete.setOnClickListener {
            viewModel.requestPlaceDeleting()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}