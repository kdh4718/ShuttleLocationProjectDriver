package kr.rabbito.shuttlelocationprojectdriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.shuttlelocationprojectdriver.databinding.ActivityMainBinding

//
class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    //매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.mainBtnToSendLocation.setOnClickListener {
            val intent = Intent(this, SendLocationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
    }

}
