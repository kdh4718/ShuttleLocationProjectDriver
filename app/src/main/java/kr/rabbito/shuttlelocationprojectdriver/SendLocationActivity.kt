package kr.rabbito.shuttlelocationprojectdriver

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

//import kotlinx.android.synthetic.main.activity_send_location.*

import kotlinx.coroutines.Job
import kr.rabbito.shuttlelocationprojectdriver.databinding.ActivitySendLocationBinding
import kr.rabbito.shuttlelocationprojectdriver.functions.ServiceLocation

class SendLocationActivity : AppCompatActivity() {

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


        var background : Intent = Intent(this, ServiceLocation::class.java)
        Log.d("서비스","두번째 액티비티 시작")
        binding.sendLocationBtnStartSend.setOnClickListener {

            // 백그라운드에서 위치정보를 전송하기위해 서비스인텐트 실행,전환
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(background)
                Log.d("서비스","서비스 시작,전환")
            }
        }
        binding.sendLocationBtnStopSend.setOnClickListener {
            //sendJob.cancel()
            /** sendstop버튼으로 서비스도 종료시킬려하는데
            앱을 실행중에 sednstart,sendstop하면 문제가 없음.
            --> sendjob,serviex가 모두 실행중에 있으므로 그런거같음

            ** but sendstart후 앱종료하면 sendJob은 몇 초 뒤 종료되고 서비스만 실행중에있는데
            sendstop누를시 실행중이지 않은 sendJob을 cancel 할려할때 오류가 나는거같음
            ** 위치정보 전송시스템을 서비스에서만 작동하게 하는게 필요해보임.
            서버에 데이터 보내는 시스템을 몰라서 잘 건드릴수가 없음..  **/
            stopService(background)
            Log.d("서비스","서비스 종료")
        }
    }
    //액티비티가 파괴될 때
    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조 정리
        mBinding = null
        Log.d("서비스","두번째 액티비티 종료")
        super.onDestroy()
    }
}