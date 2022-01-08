package com.weatherapp.features.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weatherapp.data.remote.RemoteState
import com.weatherapp.data.remote.model.CurrentWeatherResponse
import com.weatherapp.data.locale.WeatherCharacteristics
import com.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _getState: MutableLiveData<RemoteState> = MutableLiveData()
    val state: LiveData<RemoteState> = _getState

    @SuppressLint("CheckResult")
    fun getCurrentLocationWeatherData(currentLat: Double, currentLong: Double, isNetworkAvailable: Boolean) {
        weatherRepository.getWeatherBroadcast(currentLat, currentLong)
            .subscribe({
                _getState.value =
                    RemoteState.Success(createCurrentLocationWeatherCharacteristics(it))
            }, {
                try {
                    _getState.value = RemoteState.Failure(it.message)
                } catch (e: SocketTimeoutException) {
                    e.message?.let { it1 -> Log.e("Error ${javaClass.name}", it1) }
                }
            })
    }

    private fun createCurrentLocationWeatherCharacteristics(response: CurrentWeatherResponse): WeatherCharacteristics {
        return WeatherCharacteristics(
            response.timezone!!,
            response.main!!.temp,
            response.main!!.tempMin,
            response.main!!.tempMax,
            response.name,
            response.weather[0].description,
            response.weather[0].icon
        )
    }
}