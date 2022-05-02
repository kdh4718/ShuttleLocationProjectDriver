package kr.rabbito.shuttlelocationprojectdriver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_send_location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.rabbito.shuttlelocationprojectdriver.data.Location

class SendLocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_location)

        val ref = FirebaseDatabase.getInstance().getReference("Driver")
        val pref = getSharedPreferences("profile", MODE_PRIVATE)
        val editor = pref.edit()

        val id = ref.push().key!!
//        editor.putString("driver_id", id).apply()
        var group = "tuk"
        var location = Location("temp")

        val sendJob = GlobalScope.launch {
            var i = 0
            while (true) {
                location.location = i.toString()
                ref.child(group).child(id).setValue(location)
                delay(1000)
                i++
            }
        }

        sendLocation_btn_startSend.setOnClickListener { sendJob.start() }
        sendLocation_btn_stopSend.setOnClickListener { sendJob.cancel() }
    }
}
