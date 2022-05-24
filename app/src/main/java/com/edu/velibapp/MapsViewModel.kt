package com.edu.velibapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.velibapp.bean.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


/**
 *
 * @name:    MapsViewModel
 * @date:    2022-05-07 22:47
 * @comment:
 *
 */
class MapsViewModel :ViewModel() {
    val client = OkHttpClient()
    var gson = Gson()
    var mRefreshStation = MutableLiveData<Boolean>(false)
    var requestFailed= MutableLiveData<Boolean>()
    var mStationInfoList=ArrayList<Station>()
    private val TAG ="jcy-MapsViewModel"
    fun getStationInformation(){
        requestFailed.postValue(false)
        mRefreshStation.postValue(false)
        viewModelScope.launch(Dispatchers.IO) {

            val request: Request = Request.Builder()
                .url("https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/station_information.json")
                .build()
            client.newCall(request).enqueue(object :Callback{
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.d(TAG, "onFailure  ${e}");
                    mRefreshStation.postValue(true)
                    requestFailed.postValue(true)
                }

                override fun onResponse(call: Call?, response: okhttp3.Response?) {
                    if(response?.isSuccessful==true){
                        var json =response?.body()?.string();
                      var stationInfo=  gson.fromJson<StationInfo>(json,StationInfo::class.java)
                        if(stationInfo!=null){
                            Log.d(TAG, "station_information  ${stationInfo.data?.stations.size}");
                            mStationInfoList.clear()
                            mStationInfoList.addAll(stationInfo.data.stations)
                            Log.d(TAG, "mStationInfoList  ${mStationInfoList.size}");
                        }
                        getStationStates()
                    }


                }

            })

        }
        
    }

    fun getStationStates() {
        requestFailed.postValue(false)
        mRefreshStation.postValue(false)
        viewModelScope.launch(Dispatchers.IO) {

            val request: Request = Request.Builder()
                .url("https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/station_status.json")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.d(TAG, "onFailure  ${e}");
                    mRefreshStation.postValue(true)
                    requestFailed.postValue(true)
                }

                override fun onResponse(call: Call?, response: okhttp3.Response?) {
                    if (response?.isSuccessful == true) {
                        var json = response?.body()?.string();
                        var stationStatus =
                            gson.fromJson<StationStatus>(json, StationStatus::class.java)
                        if (stationStatus != null) {
                            Log.d(TAG, "station_status  ${stationStatus.data?.stations.size}");
                            var list : MutableList<StationState> = arrayListOf<StationState>()
                            list.addAll(stationStatus.data.stations)
                            App.mStationList.clear()
                            mStationInfoList.forEach { station ->
                               var  iterator:MutableIterator<StationState> = list.iterator();
                                while (iterator.hasNext()){
                                    var state = iterator.next();
                                    if(state.station_id==station.station_id){
                                        var detail = StationDetail(
                                            station.station_id,
                                            station.name,
                                            station.lat,
                                            station.lon,
                                            station.capacity,
                                            station.stationCode,
                                            station.rentalMethods,
                                            state.numBikesAvailable,
                                            state.num_bikes_available_types,
                                            state.numDocksAvailable,
                                            state.is_installed,
                                            state.is_returning,
                                            state.is_renting,
                                            state.last_reported);
                                        App.mStationList.add(detail)
                                        iterator.remove()
                                        break
                                    }
                                }
                            }
                        }
                    }
                    mRefreshStation.postValue(true)
                    Log.d(TAG, "onResponse  ${App.mStationList.size}");
                }

            })
        }
    }

}