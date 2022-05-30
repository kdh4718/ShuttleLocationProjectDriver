package kr.rabbito.shuttlelocationprojectdriver.functions


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kr.rabbito.shuttlelocationprojectdriver.SendLocationActivity

/** 서비스에서 위치정보 가져오는 함수 **/
                        //기존에 액티비티 정보를 인자를 필요했는데
                        //서비스는 액티비티와 별개로 서비스를 인자로 주는걸 모르겠음
                        //그래서 액티비티 정보를 주지않는 함수 따로 만들어줌.
fun getBackLocation(result:Array<Double>, locationManager: LocationManager, context: Context)
    :LocationListener /*requestLocationUdate를 종료시키기위해 리스너 반환*/ {
    val locationListener = object :LocationListener {
        override fun onLocationChanged(location: Location) {
            result[0]=location.latitude
            result[1]=location.longitude
            Log.d("서비스","주기적 갱신중")
        }
    }
    if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) { Log.d("서비스","권한설정필요") }
    locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 10000
        , 10.0f, locationListener)
    return locationListener
}