package com.edu.velibapp.bean

/**
 *
 * @name:    StationDetail
 * @date:    2022-05-08 00:03
 * @comment:
 *
 */
data class StationDetail(
    val station_id: Long,//唯一的站标识号。此编号标识 Vélib' Métropole 服务内的车站
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Long,
    val stationCode: String,
    val rentalMethods: List<String>? = null,
    val numBikesAvailable: Long,//可用的自行车数量
    val numBikesAvailableTypes: List<NumBikesAvailableType>,//可用的自行车数量
     val numDocksAvailable: Long,//可用的码头数量
    val isInstalled: Long,//二进制变量，表示是否是站。该站已部署 (1) 或仍在部署 (0)
    val isReturning: Long,//指示车站是否可以租用自行车的二进制变量（如果车站的状态是 Operative，is_renting=1）
    val isRenting: Long,//指示车站是否可以接收自行车的二进制变量（如果车站的状态为 Operative，则 is_renting=1）
    val lastReported: Long//最后一次更新的日期
) {

}