package com.rkhvstnv.pinplace.ui.allplaces

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rkhvstnv.pinplace.R
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.databinding.FragmentAllPlacesBinding
import com.rkhvstnv.pinplace.utils.ItemPlaceCallback
import com.rkhvstnv.pinplace.utils.appComponent
import javax.inject.Inject

class AllPlacesFragment : Fragment() {
    private var _binding: FragmentAllPlacesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterAll: AllPlacesAdapter

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<AllPlacesViewModel> {
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
        _binding = FragmentAllPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.allPlaces.observe(viewLifecycleOwner){
                list ->
            list?.let {
                if (it.isNotEmpty()){
                    adapterAll.updateList(it.reversed())
                    binding.rvAllPlaces.visibility = View.VISIBLE
                    binding.incNoPlaces.root.visibility = View.GONE
                } else{
                    binding.rvAllPlaces.visibility = View.GONE
                    binding.incNoPlaces.root.visibility = View.VISIBLE
                }
            }
        }

        binding.fabAddPlace.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_all_places_to_navigation_add_place
            )
        }

    }


    private fun setupRecyclerView(){
        adapterAll = AllPlacesAdapter(requireContext(), object : ItemPlaceCallback{
            override fun onClick(id: Int) {
                findNavController().navigate(AllPlacesFragmentDirections.actionNavigationAllPlacesToNavigationPlaceDetails(id))
            }

            override fun onDelete(placeEntity: PlaceEntity) {
                viewModel.requestPlaceDeleting(placeEntity)
            }
        })
        binding.rvAllPlaces.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = adapterAll
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}