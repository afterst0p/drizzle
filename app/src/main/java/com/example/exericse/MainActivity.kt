package com.example.exericse

import Client
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //val cookieManager = (applicationContext as Cookie).cookieManager
        //val cookie = Cookie()
        //val client = Client(cookie)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signUpButton = findViewById<Button>(R.id.sign_up_button)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val signInButton = findViewById<Button>(R.id.sign_in_button)
        signInButton.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        //val cookieManager = (applicationContext as Cookie).cookieManager
        val cookie = Cookie()
        val client = Client(cookie)

        //val timeWorkRequest = OneTimeWorkRequestBuilder<Worker>().build()
        val timeService = Intent(this, TimeService::class.java)
        super.onResume()
        /*
        client.loginCheck({ result ->
            println(result)
            if (!result.equals("not login")) {
                println("로그인 성공")
                val intent = Intent(this, TodaysContentActivity::class.java)
                startActivity(intent)
            } else {
                println("로그인 실패")
            }
        }, this)
        */
        //WorkManager.getInstance(this).enqueue(timeWorkRequest)
        Log.d("TimeService", "startForegroundService")
        startForegroundService(timeService)
    }
}

