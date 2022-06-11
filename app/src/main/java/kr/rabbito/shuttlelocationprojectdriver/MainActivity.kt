package kr.rabbito.shuttlelocationprojectdriver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kr.rabbito.shuttlelocationprojectdriver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    //매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private val LOCATION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        overridePendingTransition(0, 0)

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                    report.let {
                        if (report.areAllPermissionsGranted()) {
                            startLoading()
                        } else {
                            showSettingsDialog()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    showSettingsDialog()
                }
            }).check()
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed({
            val intent  = Intent(this, SendLocationActivity::class.java)
            finish()
            startActivity(intent)
        }, 1000)
    }

    private fun showSettingsDialog() {
        val dialogView = View.inflate(this, R.layout.permission_dialog, null)
        val permissionDialog = AlertDialog.Builder(this)
        val dlg = permissionDialog.create()
        val permission_btn_ok = dialogView.findViewById<TextView>(R.id.permissionDialog_btn_toSetting)
        val permission_btn_cancel = dialogView.findViewById<TextView>(R.id.permissionDialog_btn_cancel)
        permission_btn_ok.setOnClickListener {
            dlg.cancel()
            openSettings()
        }
        permission_btn_cancel.setOnClickListener {
            dlg.cancel()
            Toast.makeText(this, "앱 실행을 위해서는 위치 권한이 필요합니다. 앱을 재시작 해주세요.", Toast.LENGTH_SHORT).show()
        }
        dlg.setView(dialogView)
        dlg.show()
    }

    // 어플리케이션 정보 설정 페이지
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
