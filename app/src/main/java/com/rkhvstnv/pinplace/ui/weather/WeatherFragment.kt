package com.rkhvstnv.pinplace.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.rkhvstnv.pinplace.R
import com.rkhvstnv.pinplace.databinding.FragmentWeatherBinding
import com.rkhvstnv.pinplace.model.Current
import com.rkhvstnv.pinplace.model.Weather
import com.rkhvstnv.pinplace.model.WeatherModel
import com.rkhvstnv.pinplace.ui.BaseFragment
import com.rkhvstnv.pinplace.utils.Constants
import com.rkhvstnv.pinplace.utils.WeatherUtils
import com.rkhvstnv.pinplace.utils.appComponent
import com.rkhvstnv.pinplace.utils.loadIntImage
import javax.inject.Inject

class WeatherFragment : BaseFragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: WeatherViewModel by viewModels{
        viewModelFactory
    }

    private lateinit var hourForecastAdapter: HourForecastAdapter
    private lateinit var dayForecastAdapter: DayForecastAdapter
    private lateinit var sharedPref: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)

        /*Get Json weather data that saved in shared pref to show some information,
        * while it will be updated a little bit later */
        sharedPref = context.getSharedPreferences(Constants.WEATHER_SHARED_PREF, Context.MODE_PRIVATE)
        val jsonModel = sharedPref.getString(Constants.WEATHER_SHARED_JSON_STRING, null)
        viewModel.setWeatherModelFromJson(jsonModel)
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

        //Setup Forecast Recycler views
        setupHourForecastRv()
        setupDayForecastRv()

        currentLocationLauncher()


        viewModel.inLoading.observe(viewLifecycleOwner){
            inLoading->
            binding.swRefWeatherLayout.isRefreshing = inLoading
        }

        viewModel.loadingError.observe(viewLifecycleOwner){
            e->
            showSnackMessage(e)
        }

        viewModel.weatherModel.observe(viewLifecycleOwner){
            data->
            observeInUi(data)
            binding.nsvWeather.visibility = View.VISIBLE
        }


        binding.swRefWeatherLayout.setOnRefreshListener {
            currentLocationLauncher()
        }
    }


    /**Method set received weather data to all UI elements*/
    @SuppressLint("SetTextI18n")
    private fun observeInUi(wm: WeatherModel){
        val current: Current = wm.current
        val cWeather: Weather = current.weather[0]
        with(binding){

            //Set address based on latitude and longitude
            val address = WeatherUtils.getAddressFromLocation(
                requireContext(),
                wm.lat,
                wm.lon,
                1)
            address[0].let {
                tvCurrentLocation.text = it.countryCode + " / " + it.locality
            }
            //Card view with current weather data
            ivWeatherImage.loadIntImage(WeatherUtils.setRightImage(cWeather.id))
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
            //set corresponding color to sunrise progress bar
            if (sunriseModel.curProgress > sunriseModel.maxProgress){
                pbSunset.setIndicatorColor(
                    ContextCompat.getColor(requireContext(), R.color.moonlight))
            }

            tvSunrise.text = getString(R.string.st_sunrise) + Char(32) + sunriseModel.sunrise
            tvSunset.text = getString(R.string.st_sunset) + Char(32) + sunriseModel.sunset
        }

        //Day and hour forecasts
        hourForecastAdapter.updateList(wm.hourly)
        dayForecastAdapter.updateList(wm.daily)

        //Saves all data as Json to Shared preferences
        val weatherJsonString = Gson().toJson(wm)
        sharedPref.edit {
            putString(Constants.WEATHER_SHARED_JSON_STRING, weatherJsonString)
            apply()
        }
    }

    /** Method sets up Recycler view for Hour Forecast*/
    private fun setupHourForecastRv(){
        hourForecastAdapter = HourForecastAdapter(requireContext())
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

    /** Method sets up Recycler view for Day Forecast*/
    private fun setupDayForecastRv(){
        dayForecastAdapter = DayForecastAdapter(requireContext())
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


    private fun currentLocationLauncher() = requestCurrentLocation(object : LocationListener{
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            viewModel.updateCurrentLocation(location.latitude, location.longitude)
        }
    })

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}