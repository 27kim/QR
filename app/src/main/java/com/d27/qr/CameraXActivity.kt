package com.d27.qr

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
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
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import kotlinx.android.synthetic.main.activity_camerax.*
import android.net.wifi.WifiManager
import android.view.ViewGroup
import com.d27.qr.util.LLog

class CameraXActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10

        fun start(context: Context): Intent {
            return Intent(context, CameraXActivity::class.java)
        }
        private lateinit var preview: Preview
    }

    private val WIFICIPHER_NOPASS = "NOPASS"
    private val WIFICIPHER_WEP = 3
    private val WIFICIPHER_WPA = 2
    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camerax)

        textureView = findViewById(R.id.texture_view)

        btn_restart.setOnClickListener {
            startCamera()
        }

    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder()
            // We want to show input from back camera of the device
            .setLensFacing(CameraX.LensFacing.BACK)
            .setTargetAspectRatio(Rational(1, 1))
            .build()

        preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener { previewOutput ->
            val parent = textureView.parent as ViewGroup
            parent.removeView(textureView)
            textureView.surfaceTexture = previewOutput.surfaceTexture
            parent.addView(textureView, 0)
        }

        val imageAnalysisConfig = ImageAnalysisConfig.Builder().build()

        val qrCodeAnalyzer = QrCodeAnalyzer(preview) { qrCodes ->
            qrCodes.forEach {
                it.let {
                    when (it.valueType) {
                        FirebaseVisionBarcode.TYPE_WIFI -> {
                            it.let {
                                val ssid = it.wifi!!.ssid!!
                                val password = it.wifi!!.password!!
                                val type = it.wifi!!.encryptionType!!

                                connectToWifi(ssid, password, type = type)
                            }

                        }
                        FirebaseVisionBarcode.TYPE_URL -> {
                            val title = it.url?.title
                            val url = it.url?.url
                        }
                        FirebaseVisionBarcode.TYPE_SMS -> {
                        }
                        FirebaseVisionBarcode.TYPE_TEXT -> {
                        }
                        else -> {
                        }

                    }
//                Log.d("MainActivity", "QR Code detected: ${it.rawValue}.")
                    tv_result.text = it.rawValue
                    CameraX.unbind(preview)
                }

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

    override fun onResume() {
        super.onResume()

        tv_result.text = null

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


    fun connectToWifi(ssid: String, password: String, type: Int): WifiConfiguration {
        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()

        config.SSID = "\"" + ssid + "\""

        if (type == 1) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)

        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true
            config.wepKeys[0] = "\"" + password + "\""
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            config.wepTxKeyIndex = 0
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\""
            config.hiddenSSID = true
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            config.status = WifiConfiguration.Status.ENABLED
        }

        try {
            val wifiManager =
                this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.addNetwork(config)

            Log.d("after connecting", config.SSID + " " + config.preSharedKey)


            val list = wifiManager.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(i.networkId, true)
                    wifiManager.reconnect()
                    Log.d("re connecting", i.SSID + " " + config.preSharedKey)

                    break
                }
            }
        } catch (e: Exception) {
            LLog.e(e.toString())
        }
        return config
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