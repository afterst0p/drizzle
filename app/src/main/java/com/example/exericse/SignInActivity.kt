package com.example.exericse

import Client
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)

        val signInButton = findViewById<Button>(R.id.sign_in_button)
        val idEditText = findViewById<EditText>(R.id.id)
        val passwordEditText = findViewById<EditText>(R.id.password)

        signInButton.setOnClickListener {
            val id = idEditText.text.toString()
            val password = passwordEditText.text.toString()
            //val savedId = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE).getString("ID", null)
            //val savedPassword = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE).getString("PASSWORD", null)
            // 자동로그인 체크하면 여기서 가져온다음 로그인

            val cookie = Cookie()
            val client = Client(cookie)
            client.login({ result ->
                if(result.equals("login success")){
                    println("로그인 성공")

                    // 컨텐츠 갱신 알림 설정
                    val updateNotification = UpdateNotification(this)
                    updateNotification.schedule(0, 0)

                    val intent = Intent(this, TodaysContentActivity::class.java)
                    startActivity(intent)
                }
                else{
                    runOnUiThread{
                        Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                    println(result)
                }
            }, this, id, password)



/*
            if (id == savedId && password == savedPassword) {
                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
 */
        }
    }
}