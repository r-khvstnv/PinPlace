package com.rkhvstnv.pinplace.ui.weather

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.rkhvstnv.pinplace.databinding.FragmentWeatherBinding
import com.rkhvstnv.pinplace.utils.appComponent
import javax.inject.Inject

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: WeatherViewModel by viewModels{
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.inLoading.observe(viewLifecycleOwner){
            inLoading->

            Log.i("test_loading", inLoading.toString())
        }

        viewModel.loadingError.observe(viewLifecycleOwner){
            e->
            Log.i("test_loading_error", e.toString())
        }

        viewModel.weatherModel.observe(viewLifecycleOwner){
            data->
            Log.i("test_model", data.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}