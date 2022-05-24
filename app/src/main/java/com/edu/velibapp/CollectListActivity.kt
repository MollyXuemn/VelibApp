package com.edu.velibapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edu.velibapp.bean.StationDetail
import com.edu.velibapp.databinding.ActivityCollectListBinding
import com.edu.velibapp.databinding.ItemCollectBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * @name:    CollectListActivity
 * @date:    2022-05-09 22:45
 * @comment:
 *
 */
class CollectListActivity:AppCompatActivity() {
    private lateinit var binding: ActivityCollectListBinding;
    private lateinit var mDBHelper: DBHelper
    private var mStationList = arrayListOf<StationDetail>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDBHelper = DBHelper(this)
        binding.ivBack.setOnClickListener { finish() }
        binding.lvCollect.adapter  = mAdapter;
        refreshList()
        binding.lvCollect.setOnItemClickListener { adapterView, view, position, l ->
            var detail =mStationList[position]
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否取消收藏？")
                .setPositiveButton("取消收藏"){
                        dialog,i->
                    var isUnCollect = mDBHelper.unCollect(detail.station_id)
                    if(isUnCollect){
                        Toast.makeText(this, "取消收藏成功", Toast.LENGTH_SHORT).show()
                        mStationList.removeAt(position)
                        mAdapter.notifyDataSetChanged()
                    }else{
                        Toast.makeText(this, "取消收藏失败", Toast.LENGTH_SHORT).show()
                    }


                }
                .setNegativeButton("取消"){
                    dialog,i->
                    dialog.dismiss()
                }.create().show()
        }
    }


    private fun refreshList(){
       lifecycleScope.launch (Dispatchers.IO){
           for(stationId in mDBHelper.collectList){
               for (detail in App.mStationList){
                   if(stationId==detail.station_id){
                       mStationList.add(detail)
                       break
                   }
               }
           }
           withContext(Dispatchers.Main){
               mAdapter.notifyDataSetChanged()
           }
       }
    }

    private var mAdapter =object :BaseAdapter(){
        override fun getCount()=mStationList.size

        override fun getItem(p0: Int)=mStationList[p0]

        override fun getItemId(p0: Int)=p0.toLong()

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
           var itemBinding = ItemCollectBinding.inflate(LayoutInflater.from(this@CollectListActivity),p2,false)
            var detail =mStationList[p0]
            var ebikeNum = 0
            //自行车
            detail.numBikesAvailableTypes?.forEach {
                if (it.ebike != null) {
                    ebikeNum = (it.ebike + ebikeNum).toInt();
                }
            }
            var mechanicalNum = 0
            //电动车
            detail.numBikesAvailableTypes?.forEach {
                if (it.mechanical != null) {
                    mechanicalNum = (it.mechanical + mechanicalNum).toInt();
                }
            }
            //车站
            var parkingNum = detail.numDocksAvailable?.toInt();
            itemBinding.ebikeTv.setText(ebikeNum.toString())
            itemBinding.mechanicalTv.setText(mechanicalNum.toString())
            itemBinding.parkingTv.setText(parkingNum.toString())
            itemBinding.titleTv.setText(detail.name)
            itemBinding.tvStationId.setText(detail.stationCode)
            return itemBinding.root
        }
    }
}