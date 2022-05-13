package kr.rabbito.shuttlelocationprojectdriver

import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

//import kotlinx.android.synthetic.main.activity_send_location.*

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.rabbito.shuttlelocationprojectdriver.data.Location
import kr.rabbito.shuttlelocationprojectdriver.databinding.ActivitySendLocationBinding
import kr.rabbito.shuttlelocationprojectdriver.functions.getLocation

class SendLocationActivity : AppCompatActivity() {
    private lateinit var sendJob: Job

    //전역 변수로 binding 객체 선언
    private var mBinding: ActivitySendLocationBinding? = null
    //매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //자동 생성된 view Binding 클래스에서 inflate라는 메소드를 활용해서
        //약티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivitySendLocationBinding.inflate(layoutInflater)

        //getRoot 메소드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를액티비티에 표시
        setContentView(binding.root)

        //setContentView(R.layout.activity_send_location)

        val ref = FirebaseDatabase.getInstance().getReference("Driver")
        val pref = getSharedPreferences("profile", MODE_PRIVATE)
        val editor = pref.edit()

        val id = ref.push().key!!
//        editor.putString("driver_id", id).apply()
        val name = "temp"
        val group = "tuk"
        val location = Location(id, name, 0.0, 0.0)

        // 위치 리스너 등록
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val result = arrayOf(0.0, 0.0)  // 경도, 위도 저장되는 배열
        getLocation(result, locationManager, this, this)

        binding.sendLocationBtnStartSend.setOnClickListener {
            sendJob = makeLocationSendRoutine(ref, location, group, id, result)
            sendJob.start()
        }
        binding.sendLocationBtnStopSend.setOnClickListener { sendJob.cancel() }
    }

    private fun makeLocationSendRoutine(ref: DatabaseReference, location: Location, group: String, id: String, data: Array<Double>): Job {
        return GlobalScope.launch {
            while (true) {
                location.latitude = data[0]
                location.longitude = data[1]

                ref.child(group).child(id).setValue(location)
                delay(1000)
            }
        }
    }



    //액티비티가 파괴될 때
    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조 정리
        mBinding = null
        super.onDestroy()


    }

}