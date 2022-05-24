package com.edu.velibapp

import android.app.Application
import com.edu.velibapp.bean.StationDetail

/**
 *
 * @name:    App
 * @date:    2022-05-09 22:51
 * @comment:
 *
 */
class App :Application(){
    companion object{

        var mStationList=ArrayList<StationDetail>()
    }
    override fun onCreate() {
        super.onCreate()

    }
}