package com.edu.velibapp.bean

data class StationStatus (
    val lastUpdatedOther: Long,
    val ttl: Long,
    val data: Datas
)

data class Datas (
    val stations: List<StationState>
)

data class StationState (
    val stationCode: String,
    val station_id: Long,
    val numBikesAvailable: Long,
    val num_bikes_available_types: List<NumBikesAvailableType>,
    val numDocksAvailable: Long,
    val is_installed: Long,
    val is_returning: Long,
    val is_renting: Long,
    val last_reported: Long
)

data class NumBikesAvailableType (
    val mechanical: Long? = null,
    val ebike: Long? = null
)
