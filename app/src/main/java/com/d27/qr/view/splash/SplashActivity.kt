package com.d27.qr.view.splash

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.d27.qr.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {


    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {



            when(it.itemId){
                R.id.action_favorites -> {
                    Toast.makeText(applicationContext, "favorite", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_music -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, ScanQrFragment.newInstance()).commit()
                    Toast.makeText(applicationContext, "action_music", Toast.LENGTH_SHORT).show()

                    true
                }
                R.id.action_schedules -> {
                    Toast.makeText(applicationContext, "action_schedules", Toast.LENGTH_SHORT).show()

                    true
                }
                else -> false
            }
        }

    }
}
