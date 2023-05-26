package com.example.exericse

import Client
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val cookieManager = (applicationContext as Cookie).cookieManager
        val client = Client(cookieManager)

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
    override fun onResume() {
        val cookieManager = (applicationContext as Cookie).cookieManager
        val client = Client(cookieManager)

        super.onResume()
        client.loginCheck { result ->
            println(result)
            if (!result.equals("not login")) {
                println("로그인 성공")
                val intent = Intent(this, TodaysContentActivity::class.java)
                startActivity(intent)
            } else {
                println("로그인 실패")
            }
        }
    }
}

