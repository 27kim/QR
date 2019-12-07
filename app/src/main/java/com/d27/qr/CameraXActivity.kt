package com.d27.qr

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.os.Bundle
import android.util.Rational
import android.view.TextureView
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import kotlinx.android.synthetic.main.activity_camerax.*
import android.net.wifi.WifiManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.d27.qr.util.LLog
import kotlinx.android.synthetic.main.activity_camerax.view.*


class CameraXActivity : Fragment() {

    private val WIFICIPHER_NOPASS = "NOPASS"
    private val WIFICIPHER_WEP = 3
    private val WIFICIPHER_WPA = 2
    private lateinit var textureView: TextureView
    private lateinit var preview: Preview

    var ssid = ""
    var password = ""
    var type = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_camerax, container, false)

        textureView = view.findViewById(R.id.texture_view)

        view.btn_connect_to_wifi.visibility = View.VISIBLE
        view.btn_connect_to_wifi.setOnClickListener {
            connectToWifi(ssid, password, type)
        }

        return view
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
            CameraX.unbindAll()
            qrCodes[0].let {
                tv_result.text = it.rawValue
                when (it.valueType) {
                    FirebaseVisionBarcode.TYPE_WIFI -> {
                        it.let {
                            ssid = it.wifi!!.ssid!!
                            password = it.wifi!!.password!!
                            type = it.wifi!!.encryptionType!!
                            btn_connect_to_wifi.visibility = View.VISIBLE
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
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()

        tv_result.text = null

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            textureView.post { startCamera() }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
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
                Toast.makeText(activity, "Camera permission is required.", Toast.LENGTH_SHORT)
                    .show()
                activity!!.finish()
            }
        }
    }

    private fun connectToWifi(ssid: String, password: String, type: Int) {
        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()

        config.SSID = "\"" + ssid + "\""
        config.preSharedKey = "\"" + password + "\""
        config.status = WifiConfiguration.Status.ENABLED
        config.priority = 40


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
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        }

        try {

            val wifiManager: WifiManager =
                context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager

            val netId = wifiManager.addNetwork(config)

            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
        } catch (e: Exception) {
            LLog.e(e.toString())
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10

        fun start(context: Context): Intent {
            return Intent(context, CameraXActivity::class.java)
        }

        fun newInstance(): Fragment {
            return CameraXActivity()
        }
    }
}