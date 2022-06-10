package kr.rabbito.shuttlelocationprojectdriver.functions

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.rabbito.shuttlelocationprojectdriver.MainActivity
import kr.rabbito.shuttlelocationprojectdriver.data.Location

class ServiceLocation2 : Service() {
    private lateinit var sendJob: Job
    //private lateinit var locationManager : LocationManager
    //private lateinit var loc : LocationListener
    private lateinit var manager: NotificationManager
    val mid= arrayOf(37.3456, 126.7392)     //운전자가 범위내에 있는지 확인을 위한 중간지점.
    var result = arrayOf(37.3395, 126.7325) //초기값을 0,0 -> 학교정류장으로 변경.
    private lateinit var timer: CountDownTimer //일정시간 이후 스레드 종료를 위한 타이머객체 변수.

    //private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    lateinit var notification : Notification

    private lateinit var ref: DatabaseReference
    var id = "temp"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //서비스가 호출될때?마다 실행됨. 여러번 호출가능하므로 여러개의 쓰레드가
        //실행될 가능성이 있음. 그래서 위치정보 전송은 onCreate()에서 실행됨.
        if(intent==null) Service.START_STICKY
        Log.d("서비스","서비스 onStartCommand()")

        //onStartCommand()에서는 notification만 만들어줌.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel("CHANNEL_ID",
                "알림 설정 모드 타이틀",
                NotificationManager.IMPORTANCE_DEFAULT)
            //val manager = getSystemService(NotificationManager::class.java)!!
            manager = getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("셔틀위치알리미")
            .setContentText("실시간 위치 정보를 전송 중입니다.")
            .setSmallIcon(resources.getIdentifier("location_icon", "drawable", this.packageName))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(false)
            .build()
        startForeground(100, notification)
        //여기까지 notification띄우기
        Log.d("서비스","notification띄움")
        //onStartCommand()는 정수반환함.
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onCreate() {
        //서비스 생성주기중 생성될때 실행되는부분.
        super.onCreate()
        Log.d("서비스","서비스 onCreate()")

        //여기부터 위치정보 전송
        ref = FirebaseDatabase.getInstance().getReference("Driver")
        val first = getSharedPreferences("basic", Service.MODE_PRIVATE)
        val pref = getSharedPreferences("profile", Service.MODE_PRIVATE)

        // 앱 설치한 이후 최초로 실행한 서비스 인지 확인해 아이디 생성, 저장 또는 로드
        id = if (first.getBoolean("isFirst", true)) {
            ref.push().key!!
        } else {
            pref.getString("user_id", "temp")!!
        }
        if (first.getBoolean("isFirst", true)) {
            pref.edit().putString("user_id", id).apply()
            first.edit().putBoolean("isFirst", false).apply()
        }

        val name = "셔틀"
        val group = "tuk"
        val location = Location(id, name, 0.0, 0.0)
        sendJob = makeLocationSendRoutine(ref, location, group, id, result)
        sendJob.start()
        //loc = getBackLocation(result, locationManager, this)
        Log.d("서비스","위치정보전송 시작?")


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update result array with location data
                    result[0]=location.latitude
                    result[1]=location.longitude
                    Log.d("서비스","callback")
                    break
                }
            }
        }
        startLocationUpdates()
        var intent : Intent = Intent("service_down")
        intent.putExtra("data", "service")

        //타이머 객체 시간,주기,할일 설정 -- 8시간(28800초)동안,10분 주기
        timer = object : CountDownTimer(1000*600*6*8,1000*6){
            override fun onTick(p0: Long) {//주기마다 할일 메소드
                //범위내에 있는지 체크, 벗어날 시 서비스 종료.
                if(getDistance(result,mid)>1200) LocalBroadcastManager.getInstance(this@ServiceLocation2).sendBroadcast(intent)
            }
            override fun onFinish() {//타이머 종료시 할일 메소드
                Log.d("서비스","타이머종료")
                onDestroy() //타이머 종료시 서비스 종료시킴
            }
        }.start()
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        // 서비스 생성주기중 종료될때 실행되는 메소드.
        super.onDestroy()
        Log.d("서비스","서비스 onDestroy()")

        // 그룹명은 임시로 사용
        // 서비스 종료 시 위치 정보 제거
        ref.child("tuk").child(id).removeValue()

        sendJob.cancel()    //location 정보 전송을 종료시킴.
        timer.cancel()    //서비스 종료시 타이머도 종료시킴.

        //강제종료가 되어 서비스가 종료됨을 알리는 노티 띄움.
        notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("셔틀위치알리미")
            .setContentText("실시간 위치 정보 전송 종료.")
            .setSmallIcon(resources.getIdentifier("location_icon", "drawable", this.packageName))
            .build()
        startForeground(100,notification)

        /** 서비스가 종료될때 액티비티에 브로드 캐스트 전송 **/

        Log.d("서비스","브로드캐스트 전송")
    }
    private fun makeLocationSendRoutine(ref: DatabaseReference, location: Location, group: String, id: String, data: Array<Double>): Job {
        return GlobalScope.launch {
            var i:Int = 1
            while (true) {
                location.latitude = data[0]
                location.longitude = data[1]

                ref.child(group).child(id).setValue(location)
                delay(1000)

                Log.d("서비스","전송중$i, 위도경도:${location.latitude},${location.longitude}")
                i++
            }
        }
    }
    /** FLP(fusedLocationProvider객체의 업데이트를 설정합니다. **/
    private fun createLocationRequest() : LocationRequest? {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 2000     //주기(시간) ms단위
            //fastestInterval = 5000    //interval보다 더 섬세한,빠른 업데이트가 필요할 시 설정.
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY   //위치정보의 퀄리티 설정. 높을 수록 베터리 소모
        }
        Log.d("서비스","createLocationRequest()")
        return locationRequest  //설정이 완료된 request객체를 반환.
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission( //퍼미션이 설정되어있는지 확인합니다.
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { //퍼미션이 요구될경우 메시지를 출력합니다.
            Toast.makeText(this,"권한설정이 필요합니다.",Toast.LENGTH_SHORT).show()
        }
        // 퍼미션이 설정되어있으면 FLP(fusedLocationProvider)객체를 생성하여 자동갱신을 시작시킵니다.
        var fusedLocationProviderClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), //requset객체를 설정하여 가져옵니다.
            locationCallback,
            Looper.getMainLooper())
        Log.d("서비스","startLocationUpdates()")
    }
}
