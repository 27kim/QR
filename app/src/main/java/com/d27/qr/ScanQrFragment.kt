package com.d27.qr

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_scan_qr.view.*

class ScanQrFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_scan_qr, container, false)


        view.lottie.apply {
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

        view.btn1.setOnClickListener {
            startActivity(CameraXActivity.start(context!!))
        }
        view.btn2.setOnClickListener {
            startActivity(MLKitActivity.start(context!!))
        }
        view.btn3.setOnClickListener {
            startActivity( Intent(context, WifiConnect::class.java))
        }

        return view
    }

    companion object{
        fun newInstance() : Fragment{
            return ScanQrFragment()
        }
    }
}