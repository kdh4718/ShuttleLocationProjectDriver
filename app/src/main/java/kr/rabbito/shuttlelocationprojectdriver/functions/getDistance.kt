package kr.rabbito.shuttlelocationprojectdriver.functions

import android.location.Location
import android.util.Log
import kotlin.math.roundToInt

fun getDistance(shuttle:Array<Double>,target:Array<Double>):Int{
    var distance:Int
    //셔틀위치정보(배열)를(을) locShuttle객체에 저장
    var locShuttle: Location = Location("point A")
    locShuttle.latitude=shuttle[0]
    locShuttle.longitude=shuttle[1]

    //내부 위치정보(배열)를(을) locCheck객체에 저장
    var locCheck: Location = Location("point B")
    locCheck.latitude=target[0]
    locCheck.longitude=target[1]

    //셔틀위치와 꺾이는 교차로(target)위치의 거리계산
    distance = locShuttle.distanceTo(locCheck).toDouble().roundToInt()
    Log.d("서비스","거리 : $distance")
    //거리반환
    return distance
}