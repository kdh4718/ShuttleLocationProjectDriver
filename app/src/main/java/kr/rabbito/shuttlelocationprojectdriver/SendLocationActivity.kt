package kr.rabbito.shuttlelocationprojectdriver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

//import kotlinx.android.synthetic.main.activity_send_location.*

import kr.rabbito.shuttlelocationprojectdriver.databinding.ActivitySendLocationBinding
import kr.rabbito.shuttlelocationprojectdriver.functions.*

class SendLocationActivity : AppCompatActivity() {

    //전역 변수로 binding 객체 선언
    private var mBinding: ActivitySendLocationBinding? = null
    //매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    lateinit var background : Intent
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0
    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //자동 생성된 view Binding 클래스에서 inflate라는 메소드를 활용해서
        //약티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivitySendLocationBinding.inflate(layoutInflater)

        //getRoot 메소드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를액티비티에 표시
        setContentView(binding.root)

        overridePendingTransition(0, 0)

        background = Intent(this, ServiceLocation2::class.java)

        Log.d("서비스","두번째 액티비티 시작")
        binding.sendLocationBtnStartSend.setOnClickListener {
            // 디자인
            binding.sendLocationTvStart.setTextColor(Color.parseColor("#757575"))
            binding.sendLocationTvStartDetail.setTextColor(Color.parseColor("#A4A4A4"))
            binding.sendLocationIvIconGreen.setImageResource(R.drawable.sendlocation_icon_marker_green_clicked)
            binding.sendLocationBtnStartSend.setBackgroundResource(R.drawable.sendlocation_btn_send_clicked)
            binding.sendLocationBtnStartSend.isClickable = false

            binding.sendLocationTvStop.setTextColor(resources.getColorStateList(R.color.tv_d_buttontext_black))
            binding.sendLocationTvStopDetail.setTextColor(resources.getColorStateList(R.color.tv_d_buttontext_drakgray))
            binding.sendLocationIvIconRed.setImageResource(R.drawable.d_btnicon_marker_red)
            binding.sendLocationBtnStopSend.setBackgroundResource(R.drawable.d_btn_send)
            binding.sendLocationBtnStopSend.isClickable = true
            /** 버튼상태 저장(시작-off,스탑-on) **/
            val start = arrayOf(false,true)
            saveData(start,this)

            // 백그라운드에서 위치정보를 전송하기위해 서비스인텐트 실행,전환
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(background)
                Log.d("서비스","StartButton")
                Toast.makeText(applicationContext, "위치정보 전송을 시작합니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                startService(background)
                Log.d("서비스", "8.0이하 서비스 시작,전환")
            }
        }
        binding.sendLocationBtnStopSend.setOnClickListener {
            // 디자인
            binding.sendLocationTvStart.setTextColor(resources.getColorStateList(R.color.tv_d_buttontext_black))
            binding.sendLocationTvStartDetail.setTextColor(resources.getColorStateList(R.color.tv_d_buttontext_drakgray))
            binding.sendLocationIvIconGreen.setImageResource(R.drawable.d_btnicon_marker_green)
            binding.sendLocationBtnStartSend.setBackgroundResource(R.drawable.d_btn_send)
            binding.sendLocationBtnStartSend.isClickable = true

            binding.sendLocationTvStop.setTextColor(Color.parseColor("#757575"))
            binding.sendLocationTvStopDetail.setTextColor(Color.parseColor("#A4A4A4"))
            binding.sendLocationIvIconRed.setImageResource(R.drawable.sendlocation_icon_marker_red_clicked)
            binding.sendLocationBtnStopSend.setBackgroundResource(R.drawable.sendlocation_btn_send_clicked)
            binding.sendLocationBtnStopSend.isClickable = false
            /** 버튼상태저장(시작-on,스탑-off) **/
            val stop=arrayOf(true,false)
            saveData(stop,this)

            /** sendstop버튼으로 서비스도 종료시킬려하는데
            앱을 실행중에 sednstart,sendstop하면 문제가 없음.
            --> sendjob,serviex가 모두 실행중에 있으므로 그런거같음

            ** but sendstart후 앱종료하면 sendJob은 몇 초 뒤 종료되고 서비스만 실행중에있는데
            sendstop누를시 실행중이지 않은 sendJob을 cancel 할려할때 오류가 나는거같음
            ** 위치정보 전송시스템을 서비스에서만 작동하게 하는게 필요해보임.
            서버에 데이터 보내는 시스템을 몰라서 잘 건드릴수가 없음..  **/
            stopService(background)
            Log.d("서비스","StopButton")
            Toast.makeText(this,"위치 전송이 중단되었습니다.",Toast.LENGTH_SHORT).show()
        }
        binding.sendLocationBtnToSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    //서비스가 종료될때 브로드 캐스트를 수신한다.
    var receiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            var servData = intent.getStringExtra("data")
            Log.d("서비스","브로드캐스트 수신")
            //브로드 캐스트를 수신하면 stopButton을 누른다.
            binding.sendLocationBtnStopSend.callOnClick()
        }
    }

    //onResume 호출될때 로컬브로드 캐스트 등록
    override fun onResume() {
        super.onResume()
        var intentFilter : IntentFilter = IntentFilter()
        intentFilter.addAction("service_down")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

        /** 액티비티 재실행시 버튼상태를 load해서 세팅한다. **/
        loadData(this,binding)
        Log.d("서비스","onResume()")
    }

    // 뒤로가기 두 번 연속 터치 시 종료
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - backPressedTime
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            // 뒤로가기 버튼으로 종료 시 전송 서비스도 종료
            binding.sendLocationBtnStopSend.callOnClick()
            Log.d("서비스","서비스 종료")
            finish()
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조 정리
        mBinding = null
        stopService(background)
        Log.d("서비스","두번째 액티비티 종료")
        val stop = arrayOf(true,false)
        saveData(stop,this@SendLocationActivity)
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}