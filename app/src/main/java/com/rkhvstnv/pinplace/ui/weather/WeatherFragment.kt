package com.rkhvstnv.pinplace.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rkhvstnv.pinplace.R
import com.rkhvstnv.pinplace.databinding.FragmentWeatherBinding
import com.rkhvstnv.pinplace.model.Current
import com.rkhvstnv.pinplace.model.Weather
import com.rkhvstnv.pinplace.model.WeatherModel
import com.rkhvstnv.pinplace.utils.WeatherUtils
import com.rkhvstnv.pinplace.utils.appComponent
import com.rkhvstnv.pinplace.utils.loadImage
import javax.inject.Inject

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: WeatherViewModel by viewModels{
        viewModelFactory
    }
    private lateinit var hourForecastAdapter: HourForecastAdapter
    private lateinit var dayForecastAdapter: DayForecastAdapter

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
        hourForecastAdapter = HourForecastAdapter(requireContext())
        dayForecastAdapter = DayForecastAdapter(requireContext())
        setupHourForeCastRv()
        setupDayForeCastRv()

        viewModel.inLoading.observe(viewLifecycleOwner){
            inLoading->
            binding.srWeatherLayout.isRefreshing = inLoading

            if (inLoading){
                binding.nsvWeather.visibility = View.GONE
            } else{
                binding.nsvWeather.visibility = View.VISIBLE
            }
        }

        viewModel.loadingError.observe(viewLifecycleOwner){
            e->
            Log.i("test_loading_error", e.toString())//todo
        }

        viewModel.weatherModel.observe(viewLifecycleOwner){
            data->
            observeInUi(data)
        }

        binding.srWeatherLayout.setOnRefreshListener {
            viewModel.updateDishData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun observeInUi(wm: WeatherModel){
        val current: Current = wm.current
        val cWeather: Weather = current.weather[0]
        with(binding){
            //Card view with current data
            ivWeatherImage.loadImage(WeatherUtils.setRightImage(cWeather.id))
            tvWeatherTitle.text = cWeather.main
            tvDayName.text = WeatherUtils.getDayName(System.currentTimeMillis() / 1000L)
            tvTemperature.text = current.temp.toInt().toString()
            tvFeelsLike.text = getString(R.string.st_feels_like) +
                    Char(32) + current.feels_like.toInt() + getString(R.string.st_celsius_sign)

            //Weather Details
            tvWind.text = WeatherUtils.getRoundedDouble(current.wind_speed) +
                    Char(32) + getString(R.string.st_km_per_hour)
            tvHumidity.text = current.humidity.toString() + Char(32) + Char(37)
            tvPressure.text = current.pressure.toString() + Char(32) + getString(R.string.st_m_bar)

            //Sunrise Progress
            val sunriseModel =
                WeatherUtils.getSunriseProgress(current.sunrise.toLong(), current.sunset.toLong())
            pbSunset.max = sunriseModel.maxProgress
            pbSunset.progress = sunriseModel.curProgress
            if (sunriseModel.curProgress > sunriseModel.maxProgress){
                pbSunset.setIndicatorColor(
                    ContextCompat.getColor(requireContext(), R.color.moonlight))
            }

            tvSunrise.text = getString(R.string.st_sunrise) + Char(32) + sunriseModel.sunrise
            tvSunset.text = getString(R.string.st_sunset) + Char(32) + sunriseModel.sunset
        }

        hourForecastAdapter.updateList(wm.hourly)
        dayForecastAdapter.updateList(wm.daily)
    }

    private fun setupHourForeCastRv(){
        binding.rvHourForecast.apply {
            adapter = hourForecastAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
        }
    }

    private fun setupDayForeCastRv(){
        binding.rvDayForecast.apply {
            adapter = dayForecastAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            setHasFixedSize(true)
        }
    }
}