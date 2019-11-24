package com.d27.qr.view.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.d27.qr.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class SplashActivity : AppCompatActivity() {


    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {



            when(it.itemId){
                R.id.menu_scan_qr -> {
                    Toast.makeText(applicationContext, "favorite", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_info -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, ScanQrFragment.newInstance()).commit()
                    Toast.makeText(applicationContext, "action_music", Toast.LENGTH_SHORT).show()

                    true
                }
                R.id.menu_generate_qr -> {
                    Toast.makeText(applicationContext, "action_schedules", Toast.LENGTH_SHORT).show()

                    true
                }
                else -> false
            }
        }

    }
}
