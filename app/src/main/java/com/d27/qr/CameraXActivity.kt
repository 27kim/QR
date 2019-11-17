package com.d27.qr

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.TextureView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.activity_camerax.*

class CameraXActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10

        fun start(context: Context): Intent {
            return Intent(context, CameraXActivity::class.java)
        }
    }

    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camerax)

        textureView = findViewById(R.id.texture_view)

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            textureView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder()
            // We want to show input from back camera of the device
            .setLensFacing(CameraX.LensFacing.BACK)
            .setTargetAspectRatio(Rational(1, 1))
            .build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener { previewOutput ->
            textureView.surfaceTexture = previewOutput.surfaceTexture
        }

        val imageAnalysisConfig = ImageAnalysisConfig.Builder().build()

        val qrCodeAnalyzer = QrCodeAnalyzer { qrCodes ->
            qrCodes.forEach {
//                Log.d("MainActivity", "QR Code detected: ${it.rawValue}.")
                tv_result.text = it.rawValue
            }
        }

        val imageAnalysis = ImageAnalysis(imageAnalysisConfig).apply {
            analyzer = qrCodeAnalyzer
        }

        // We need to bind preview and imageAnalysis use cases
        CameraX.bindToLifecycle(this as LifecycleOwner, preview, imageAnalysis)
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                textureView.post { startCamera() }
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}
//
//    lateinit var textureView: TextureView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_camerax)
//        textureView = findViewById(R.id.texture_view)
//
//        if (allPermissionsGranted()) {
//            textureView.post { startCamera() }
//        } else {
//            getRuntimePermissions()
//        }
//
//
//    }
//
//    private fun startCamera() {
//        val previewConfig = PreviewConfig.Builder()
//            .setLensFacing(CameraX.LensFacing.BACK)
//            .build()
//
//        val preview = Preview(previewConfig)
//
//        preview.setOnPreviewOutputUpdateListener { previewOutPut ->
//            textureView.surfaceTexture = previewOutPut.surfaceTexture
//        }
//
//        val imageAnalysisConfig = ImageAnalysisConfig.Builder()
//            .build()
//        var imageAnalysis = ImageAnalysis(imageAnalysisConfig)
//
//        val qrCodeAnalyzer = QrCodeAnalyzer { qrCodes ->
//            qrCodes.forEach {
//                LLog.d("QR Code detected : ${it.rawValue}")
//            }
//        }
//
//        imageAnalysis.analyzer = qrCodeAnalyzer
//
////        CameraX.bindToLifecycle(this as LifecycleOwner, preview)
//    }
//
//    private val requiredPermissions: Array<String?>
//        get() {
//            return try {
//                val info = this.packageManager
//                    .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
//                val ps = info.requestedPermissions
//                if (ps != null && ps.isNotEmpty()) {
//                    ps
//                } else {
//                    arrayOfNulls(0)
//                }
//            } catch (e: Exception) {
//                arrayOfNulls(0)
//            }
//        }
//
//    private fun allPermissionsGranted(): Boolean {
//        for (permission in requiredPermissions) {
//            if (!CameraXActivity.isPermissionGranted(this, permission!!)) {
//                return false
//            }
//        }
//        return true
//    }
//
//    private fun getRuntimePermissions() {
//        val allNeededPermissions = arrayListOf<String>()
//        for (permission in requiredPermissions) {
//            if (!CameraXActivity.isPermissionGranted(this, permission!!)) {
//                allNeededPermissions.add(permission)
//            }
//        }
//
//        if (allNeededPermissions.isNotEmpty()) {
//            ActivityCompat.requestPermissions(
//                this, allNeededPermissions.toTypedArray(), CameraXActivity.PERMISSION_REQUESTS
//            )
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        Log.i("TAG", "Permission granted!")
//        if (allPermissionsGranted()) {
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    companion object {
//        private const val PERMISSION_REQUESTS = 1
//
//        fun start(context: Context): Intent {
//            return Intent(context, CameraXActivity::class.java)
//        }
//
//        private fun isPermissionGranted(context: Context, permission: String): Boolean {
//            if (ContextCompat.checkSelfPermission(
//                    context,
//                    permission
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                Log.i("TAG", "Permission granted: $permission")
//                return true
//            }
//            Log.i("TAG", "Permission NOT granted: $permission")
//            return false
//        }
//    }
//}