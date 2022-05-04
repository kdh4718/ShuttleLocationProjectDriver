package kr.rabbito.shuttlelocationprojectdriver.data

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kr.rabbito.shuttlelocationprojectdriver.R

class getLocationActivity : AppCompatActivity() {
    lateinit var button : Button    //버튼을 눌러 현재 위치정보 출력
    var longitude:Double?=null      //경도
    var latitude:Double?=null       //위도

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_location)
        button = findViewById<Button>(R.id.button)

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager


        //위치 리스너(Listener)구문
        //일정 시간 동안 반복해서 위치 정보를 얻어와야 할 때는
        //LocationListener를 사용합니다.
        //getLastKnownLocation 사용시에는 주석처리.
        val locationListener = object : LocationListener {

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                //super.onStatusChanged(provider, status, extras)
                // provider의 상태가 변경될때마다 호출
                // deprecated
            }

            override fun onLocationChanged(location: Location) {
                // 위치 정보 전달 목적으로 호출(자동으로 호출)
                longitude = location.longitude
                latitude = location.latitude

                Log.d("LotLog", "Latitude : $latitude, Longitude : $longitude")
            }
            override fun onProviderEnabled(provider: String) {
                // provider가 사용 가능한 생태가 되는 순간 호출
            }
            override fun onProviderDisabled(provider: String) {
                // provider가 사용 불가능 상황이 되는 순간 호출
            }
        }

        //매니페스트에서 퍼미션 설정해도 코드내에서 한번더 확인해서 다이얼로그 띄워주기
        //없으면 오류 뜸!
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            var permissions = arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION )

            ActivityCompat.requestPermissions(this, permissions,100)
        }


        //Listener를 사용하여 위치를 갱신할 주기를 설정
        //Listener 미사용시 주석처리
        // 매개변수로 위치 정보 제공자, LocationListener 호출 주기, 변경 위치 거리의 정보, LocationListener전달
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 10000
            , 10.0f, locationListener)

        //버튼 클릭시 현재 위도경도 출력.
        button.setOnClickListener {
            //button.setText("Latitude : $latitude Longitude : $longitude")
            Toast.makeText(this,"Latitude : $latitude Longitude : $longitude", Toast.LENGTH_SHORT).show()
        }
    }
}