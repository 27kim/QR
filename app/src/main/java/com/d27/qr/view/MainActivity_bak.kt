package com.d27.qr.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.d27.qr.R
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.vision.Frame
import android.graphics.BitmapFactory

class MainActivity_bak : AppCompatActivity() {
    companion object {
        fun start(context: Context): Intent {
            return Intent(context, MainActivity_bak::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.scale_fade_in, R.anim.no_anim)
        setContentView(R.layout.activity_main)

        val detector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
            .build()

        if (!detector.isOperational) {
            txtContent.setText("Could not set up the detector!")
            return
        }

        val myBitmap = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.puppy
        )

        val frame = Frame.Builder().setBitmap(myBitmap).build()
        val barCodes = detector.detect(frame)
        val thisCode = barCodes.valueAt(0)
        txtContent.text = thisCode.rawValue

    }
}