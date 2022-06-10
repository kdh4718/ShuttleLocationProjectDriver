package kr.rabbito.shuttlelocationprojectdriver.functions

import android.content.Context
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import kr.rabbito.shuttlelocationprojectdriver.R
import kr.rabbito.shuttlelocationprojectdriver.databinding.ActivitySendLocationBinding

fun saveData(bool: Array<Boolean>, context: Context){   //버튼상태를 저장하는 메소드
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    editor.putBoolean("start",bool[0]).putBoolean("stop",bool[1]).apply()
    Log.d("서비스","saveData ${bool[0]},${bool[1]}")
}

fun loadData(   //버튼 상태를 불러와 적용하는 메소드
    context: Context,
    binding: ActivitySendLocationBinding,
){
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val start_state = pref.getBoolean("start",true)
    val stop_state = pref.getBoolean("stop",false)
    Log.d("서비스","loadData ${start_state},${stop_state}")
    if(start_state && !stop_state){
        binding.sendLocationTvStart.setTextColor(context.resources.getColorStateList(R.color.tv_d_buttontext_black))
        binding.sendLocationTvStartDetail.setTextColor(context.resources.getColorStateList(R.color.tv_d_buttontext_drakgray))
        binding.sendLocationIvIconGreen.setImageResource(R.drawable.d_btnicon_marker_green)
        binding.sendLocationBtnStartSend.setBackgroundResource(R.drawable.d_btn_send)
        binding.sendLocationBtnStartSend.isClickable=true

        binding.sendLocationTvStop.setTextColor(Color.parseColor("#757575"))
        binding.sendLocationTvStopDetail.setTextColor(Color.parseColor("#A4A4A4"))
        binding.sendLocationIvIconRed.setImageResource(R.drawable.sendlocation_icon_marker_red_clicked)
        binding.sendLocationBtnStopSend.setBackgroundResource(R.drawable.sendlocation_btn_send_clicked)
        binding.sendLocationBtnStopSend.isClickable=false
    }
    if(!start_state && stop_state){
        binding.sendLocationBtnStartSend.isClickable=false
        binding.sendLocationTvStart.setTextColor(Color.parseColor("#757575"))
        binding.sendLocationTvStartDetail.setTextColor(Color.parseColor("#A4A4A4"))
        binding.sendLocationIvIconGreen.setImageResource(R.drawable.sendlocation_icon_marker_green_clicked)
        binding.sendLocationBtnStartSend.setBackgroundResource(R.drawable.sendlocation_btn_send_clicked)

        binding.sendLocationBtnStopSend.isClickable=true
        binding.sendLocationTvStop.setTextColor(context.resources.getColorStateList(R.color.tv_d_buttontext_black))
        binding.sendLocationTvStopDetail.setTextColor(context.resources.getColorStateList(R.color.tv_d_buttontext_drakgray))
        binding.sendLocationIvIconRed.setImageResource(R.drawable.d_btnicon_marker_red)
        binding.sendLocationBtnStopSend.setBackgroundResource(R.drawable.d_btn_send)
    }
}