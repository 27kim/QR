package com.d27.qr.view.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.d27.qr.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ScanQrFragment.newInstance()).commit()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.menu_scan_qr -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, CameraXActivity.newInstance()).commit()
                    true
                }

                R.id.menu_generate_qr -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, GenerateQrFragment.newInstance()).commit()
                    true
                }
                R.id.menu_info -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, InfoFragment.newInstance()).commit()

                    true
                }
                else -> false
            }
        }

    }
}
