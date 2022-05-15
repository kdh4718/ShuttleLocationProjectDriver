package kr.rabbito.shuttlelocationprojectdriver.functions

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.rabbito.shuttlelocationprojectdriver.MainActivity
import kr.rabbito.shuttlelocationprojectdriver.data.Location
                    /** 서비스 **/
                    //https://developer.android.com/guide/components/services?hl=ko#kotlin
                    //https://qwerty-ojjj.tistory.com/37
class ServiceLocation : Service() {
    private lateinit var sendJob: Job
    private lateinit var locationManager :LocationManager
    private lateinit var loc : LocationListener

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //서비스가 호출될때?마다 실행됨. 여러번 호출가능하므로 여러개의 쓰레드가
        //실행될 가능성이 있음. 그래서 위치정보 전송은 onCreate()에서 실행됨.
        if(intent==null) START_STICKY
        Log.d("서비스","서비스 전환됨")

        //onStartCommand()에서는 notification만 만들어줌.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel("CHANNEL_ID",
                "알림 설정 모드 타이틀",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)!!
            manager!!.createNotificationChannel(serviceChannel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("앱이름!")
            .setContentText("위치정보 갱신 중")
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //여기까지 notification띄우기
        Log.d("서비스","notification띄움")

        //onStartCommand()는 정수반환함.
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onCreate() {
        //서비스 생성주기중 생성될때 실행되는부분.
        super.onCreate()
        Log.d("서비스","서비스 생성됨")

        //여기부터 위치정보 전송
        val ref = FirebaseDatabase.getInstance().getReference("Driver")
        val id = ref.push().key!!
        val name = "temp"
        val group = "tuk"
        val location = Location(id, name, 0.0, 0.0)
        val result = arrayOf(0.0, 0.0)  // 경도, 위도 저장되는 배열
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        //getBackLocation(result, locationManager, this)
        sendJob = makeLocationSendRoutine(ref, location, group, id, result)
        sendJob.start()
        loc = getBackLocation(result, locationManager, this)
        Log.d("서비스","위치정보전송 시작?")
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        // 서비스 생성주기중 종료될때 실행되는 메소드.
        super.onDestroy()
        Log.d("서비스","서비스 파괴됨")

        //locationManager의 locationListener의 주기적 업데이트를 삭제해줌.
        locationManager.removeUpdates(loc)
        //location 정보 전송을 종료시킴.
        sendJob.cancel()
        Toast.makeText(this,"서비스 종료",Toast.LENGTH_SHORT).show()
    }
    private fun makeLocationSendRoutine(ref: DatabaseReference, location: Location, group: String, id: String, data: Array<Double>): Job {
        return GlobalScope.launch {
            var i:Int = 0
            while (true) {
                location.latitude = data[0]
                location.longitude = data[1]

                ref.child(group).child(id).setValue(location)
                delay(1000)

                Log.d("서비스","주기적 전송중 $i")
                i++
            }
        }
    }
}