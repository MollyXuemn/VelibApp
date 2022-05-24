package com.edu.velibapp.bean

data class StationInfo (
    val lastUpdatedOther: Long,
    val ttl: Long,
    val data: Data

){
    override fun toString(): String {
        return "StationInfo(lastUpdatedOther=$lastUpdatedOther, ttl=$ttl, data=$data)"
    }
}

data class Data (
    val stations: List<Station>
){
    override fun toString(): String {
        return "Data(stations=$stations)"
    }
}

data class Station (
    val station_id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Long,
    val stationCode: String,
    val rentalMethods: List<String>? = null
)
