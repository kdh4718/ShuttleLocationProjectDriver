package kr.rabbito.shuttlelocationprojectdriver

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kr.rabbito.shuttlelocationprojectdriver.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private var mBinding: ActivitySettingBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(0, 0)

        binding.settingClSettingInfo.setOnClickListener {
            val dialogView = View.inflate(this, R.layout.info_dialog, null)
            val info = AlertDialog.Builder(this)
            val dlg = info.create()
            val info_btn = dialogView.findViewById<TextView>(R.id.infodialog_btn_cancel)
            info_btn.setOnClickListener { dlg.dismiss() }
            dlg.setView(dialogView)
            dlg.show()
        }

        binding.settingBtnBack.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}