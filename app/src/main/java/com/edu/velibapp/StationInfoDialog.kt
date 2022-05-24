package com.edu.velibapp

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.edu.velibapp.bean.StationDetail
import com.edu.velibapp.databinding.DgStationInfoBinding

/**
 *
 * @name:    StationInfoDialog
 * @date:    2022-05-09 22:14
 * @comment:
 *
 */
class StationInfoDialog(context: Context) : Dialog(context, R.style.fullDialog) {
    private lateinit var binding: DgStationInfoBinding;
    var detail: StationDetail? = null
    private lateinit var mDBHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DgStationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //一定要在setContentView之后调用，否则无效
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mDBHelper = DBHelper(context)
        binding.llInfo.setOnClickListener { }
        binding.rlBg.setOnClickListener { dismiss() }
        binding.hidrRl.setOnClickListener {
            dismiss()
        }
        binding.ivCollect.setOnClickListener {
            var isCollect = binding.ivCollect.tag as Boolean
            if (isCollect) {
                mDBHelper.unCollect(detail!!.station_id)
                Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show()
            } else {
                mDBHelper.collect(detail!!.station_id)
                Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show()
            }
            updateCollectInfo()
        }
    }

    private fun updateCollectInfo() {
        if (mDBHelper.isCollect(detail!!.station_id)) {
            binding.ivCollect.setImageResource(R.drawable.ic_collect)
            binding.ivCollect.setTag(true)
        } else {
            binding.ivCollect.setTag(false)
            binding.ivCollect.setImageResource(R.drawable.ic_uncollect)

        }
    }

    override fun show() {
        super.show()
        if (detail != null) {
            var ebikeNum = 0
            //自行车
            detail?.numBikesAvailableTypes?.forEach {
                if (it.ebike != null) {
                    ebikeNum = (it.ebike + ebikeNum).toInt();
                }
            }
            var mechanicalNum = 0
            //电动车
            detail?.numBikesAvailableTypes?.forEach {
                if (it.mechanical != null) {
                    mechanicalNum = (it.mechanical + mechanicalNum).toInt();
                }
            }
            //车站
            var parkingNum = detail?.numDocksAvailable?.toInt() ?: 0;
            binding.ebikeTv.setText(ebikeNum.toString())
            binding.mechanicalTv.setText(mechanicalNum.toString())
            binding.parkingTv.setText(parkingNum.toString())
            updateCollectInfo()
            binding.titleTv.setText(detail!!.name)
            binding.tvStationId.setText(detail!!.stationCode)
        }


    }
}