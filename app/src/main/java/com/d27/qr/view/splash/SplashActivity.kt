package com.d27.qr.view.splash

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.d27.qr.CameraXActivity
import com.d27.qr.R
import com.d27.qr.MLKitActivity
import com.d27.qr.ZXingActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lottie.apply {
            setAnimation("7242-barcode-scanner.json")
            playAnimation()
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
        }

        btn1.setOnClickListener {
            startActivity(CameraXActivity.start(applicationContext))
        }
        btn2.setOnClickListener {
            startActivity(MLKitActivity.start(applicationContext))
        }
    }
}
