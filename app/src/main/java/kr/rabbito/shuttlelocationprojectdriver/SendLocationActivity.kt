package kr.rabbito.shuttlelocationprojectdriver

import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_send_location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.rabbito.shuttlelocationprojectdriver.data.Location
import kr.rabbito.shuttlelocationprojectdriver.functions.getLocation

class SendLocationActivity : AppCompatActivity() {
    private lateinit var sendJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_location)

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

        sendLocation_btn_startSend.setOnClickListener {
            sendJob = makeLocationSendRoutine(ref, location, group, id, result)
            sendJob.start()
        }
        sendLocation_btn_stopSend.setOnClickListener { sendJob.cancel() }
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
}