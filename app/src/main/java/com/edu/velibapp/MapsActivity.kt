package com.edu.velibapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.edu.velibapp.bean.StationDetail
import com.edu.velibapp.databinding.ActivityMapsBinding
import com.edu.velibapp.databinding.MarkerBinding
import com.edu.velibapp.utils.BitmapDescriptorUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit  var mDBHelper: DBHelper
    private var mMap: GoogleMap?=null
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel;
    private var mStationMarkers = arrayListOf<Marker>()
    lateinit var mAllStationBuilder: LatLngBounds.Builder
    var schoolMarker: Marker? = null;
    var mCurStationSelect = MutableLiveData<Int>(0)// 0 自行车 1 电动车 2 车站
    var mShowType=  MutableLiveData<Int>(0)// 0 学校  1 全部车站
    private var lancher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult? ->
        //drawVelibs()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDBHelper= DBHelper(this)
        viewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
        binding.icEbike.setOnClickListener { mCurStationSelect.value=0 }
        binding.icMechanical.setOnClickListener {  mCurStationSelect.value=1 }
        binding.icParking.setOnClickListener {  mCurStationSelect.value=2 }
        binding.icSchool.setOnClickListener {  mShowType.value=0 }
        binding.icAll.setOnClickListener {  mShowType.value=1 }
        binding.icCollect.setOnClickListener {
            lancher.launch(Intent(this,CollectListActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        mShowType.observe(this){
            if(it==0){
                showSchool()
                binding.icSchool.setBackgroundResource(R.color.teal_200)
                binding.icAll.setBackgroundResource(R.color.white)
            }else{
                showAllVelibs()
                binding.icSchool.setBackgroundResource(R.color.white)
                binding.icAll.setBackgroundResource(R.color.teal_200)
            }
        }
        viewModel.mRefreshStation.observe(this) {
            if (it) {
                stopLoding()
                drawVelibs()
            } else {
                startLoading()
            }
        }
        viewModel.requestFailed.observe(this) {
            if (it) {
              Toast.makeText(this,"列表请求失败，请重试",Toast.LENGTH_SHORT).show()
            }
        }
        mCurStationSelect.observe(this) {
            // 0 自行车 1 电动车 2 车站
            when (it) {
                0 -> {
                    binding.icEbike.setBackgroundResource(R.color.teal_200)
                    binding.icMechanical.setBackgroundResource(R.color.white)
                    binding.icParking.setBackgroundResource(R.color.white)
                }
                1 -> {
                    binding.icEbike.setBackgroundResource(R.color.white)
                    binding.icMechanical.setBackgroundResource(R.color.teal_200)
                    binding.icParking.setBackgroundResource(R.color.white)
                }
                2 -> {
                    binding.icEbike.setBackgroundResource(R.color.white)
                    binding.icMechanical.setBackgroundResource(R.color.white)
                    binding.icParking.setBackgroundResource(R.color.teal_200)
                }
            }
            drawVelibs()
        }
    }

    private fun showSchool(){
        if(mMap!=null){
            var schoolPoint = LatLng(48.7971909, 2.3020676);
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(schoolPoint, 20f))
        }

    }
    private fun showAllVelibs(){
        if(mMap!=null){
            if(App.mStationList.isEmpty())
            {
                return
            }
            var velibsBuilder=LatLngBounds.Builder()
            App.mStationList.forEach { station ->
                var point = LatLng(station.lat, station.lon);

                velibsBuilder.include(point)
            }
            var bounds= velibsBuilder.build()
            val padding = 0
            // offset from edges of the map in pixels
            // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(
                bounds, padding
            )
            mMap?.animateCamera(cu)
        }
    }

    private fun drawVelibs() {
        //var bitmap = BitmapDescriptorFactory.fromBitmap(getPointMarker(""))
        if (mMap != null) {
            mMap?.clear()
            mStationMarkers.clear()
            schoolMarker=null
            drawSchool()
            App.mStationList.forEach { station ->
                var point = LatLng(station.lat, station.lon);
                var num = 0
                if (mCurStationSelect.value == 0) {
                    //自行车
                    station.numBikesAvailableTypes?.forEach {
                        if(it.ebike!=null){
                            num = (it.ebike+num).toInt();
                        }
                    }
                }else  if (mCurStationSelect.value == 1) {
                    //电动车
                    station.numBikesAvailableTypes?.forEach {
                        if(it.mechanical!=null){
                            num = (it.mechanical+num).toInt();
                        }
                    }
                }else  if (mCurStationSelect.value == 2) {
                    //车站
                    num= station.numDocksAvailable.toInt();
                }
            /*    var hue=
                if(mDBHelper.isCollect(station.station_id)){
                    HUE_RED
                }else{
                    HUE_BLUE
                }*/
                mMap!!.addMarker(
                    MarkerOptions()
                      //  .icon(BitmapDescriptorFactory.fromBitmap(getPointMarker(num.toString())))
                        .icon(BitmapDescriptorFactory.defaultMarker(HUE_BLUE))
                        .position(point)
                        .title(station.name)
                )?.let { it1 ->
                    run {
                        it1.tag = station
                        mStationMarkers.add(it1)
                    }
                }
            }
        }
    }

    private fun startLoading(){
        val anim: Animation =
            AnimationUtils.loadAnimation(this, R.anim.anim_rotate)
        anim.fillAfter = true //设置旋转后停止

        binding.icRefresh.startAnimation(anim)
    }

    private fun stopLoding(){
        binding.icRefresh.clearAnimation()
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.uiSettings?.isZoomControlsEnabled =true;
        mMap?.uiSettings?.isZoomGesturesEnabled =true;
        mMap?.uiSettings?.isCompassEnabled =true;
        mMap?.uiSettings?.isRotateGesturesEnabled =true;
        mAllStationBuilder = LatLngBounds.Builder()
        viewModel.getStationInformation()
        /*

           // Add a marker in Sydney and move the camera
           val sydney = LatLng(48.7971909,  2.3020676)
           mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
          */
        showSchool()
        drawSchool()
        mMap?.setOnMarkerClickListener {
            if(it.tag is StationDetail){
                var dialog=StationInfoDialog(this)
                dialog.detail = it.tag as StationDetail
                dialog.setOnDismissListener {
                    //drawVelibs()
                }
                dialog.show()
                true
            }
            false
        }
    }


    fun drawSchool() {
        /*  if(schoolBuilder==null){
              schoolBuilder=LatLngBounds.Builder()
          }
          schoolBuilder?.include(LatLng(48.7971909,  2.3020676))
          var bounds= schoolBuilder?.build()
          val padding = 0
          // offset from edges of the map in pixels
          // offset from edges of the map in pixels
          val cu = CameraUpdateFactory.newLatLngBounds(
              bounds!!, padding
          )*/
        schoolMarker?.remove()
        var schoolPoint = LatLng(48.7971909, 2.3020676);
        schoolMarker = mMap!!.addMarker(
            MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.school))
                .position(schoolPoint)
                .title("Marker in School")
        )

       // var cu = CameraUpdateFactory.newLatLng(schoolPoint)
        // mMap.moveCamera(cu)
      // mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(schoolPoint, 20f))
        // val drawable = AppCompatResources.getDrawable(this, resId)
    }

    /**
     * 获取不同边界点
     *
     * @param name 序号
     * @return
     */
    fun getPointMarker(name: String): Bitmap {
        var markerBiding = MarkerBinding.inflate(LayoutInflater.from(this))
        val ll = markerBiding.llContent
        markerBiding.tvNum.text = name
        val bitmap = BitmapDescriptorUtils.fromView(markerBiding.root)
        return bitmap!!;
    }
}