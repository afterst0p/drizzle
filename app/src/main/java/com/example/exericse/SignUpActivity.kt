package com.example.exericse

import Client
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)


        val signUpButton = findViewById<Button>(R.id.sign_up_button)
        val newIdEditText = findViewById<EditText>(R.id.new_id)
        val newPasswordEditText = findViewById<EditText>(R.id.new_password)
        val categoryEditText = findViewById<EditText>(R.id.category)
        val nameEditText = findViewById<EditText>(R.id.name)


        signUpButton.setOnClickListener{
            val newId = newIdEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val category = categoryEditText.text.toString()
            val nickName = nameEditText.text.toString()

            val cookieManager = (applicationContext as Cookie).cookieManager
            val client = Client(cookieManager)
            client.signUp(nickName, newId, newPassword, category)


            val sharedPreferences = getSharedPreferences("MY_APP_PREFERENCES", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("NAME", nickName)
            editor.putString("ID", newId)
            editor.putString("PASSWORD", newPassword)
            editor.putString("CATEGORY", category)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}