package com.example.exericse

import Client
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)

        val signUpButton = findViewById<Button>(R.id.sign_up_button)
        val newIdEditText = findViewById<EditText>(R.id.new_id)
        val newPasswordEditText = findViewById<EditText>(R.id.new_password)
        val categoryDropdown = findViewById<Spinner>(R.id.new_category)
        val nameEditText = findViewById<EditText>(R.id.new_name)

        val categoryItems = resources.getStringArray(R.array.category_items)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryDropdown.adapter = adapter

        signUpButton.setOnClickListener{
            val newId = newIdEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val category = categoryDropdown.adapter.getItem(categoryDropdown.selectedItemPosition).toString()
            val nickName = nameEditText.text.toString()

            if (newId.length < 4) {
                Toast.makeText(
                    this@SignUpActivity,
                    " ID를 4글자 이상 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (newPassword.length < 4) {
                Toast.makeText(
                    this@SignUpActivity,
                    " 비밀번호를 4글자 이상 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (category.equals("카테고리")) {
                Toast.makeText(
                    this@SignUpActivity,
                    " 카테고리를 선택해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (nickName.length < 2) {
                Toast.makeText(
                    this@SignUpActivity,
                    " 닉네임을 2글자 이상 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //val cookieManager = (applicationContext as Cookie).cookieManager
            val cookie = Cookie()
            val client = Client(cookie)
            client.signUp(nickName, newId, newPassword, category, { result ->
                runOnUiThread {
                    if (result == false) {
                        Toast.makeText(
                            this@SignUpActivity,
                            " 이미 존재하는 ID입니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (result == null) {
                        Toast.makeText(
                            this@SignUpActivity,
                            " 회원가입에 실패하였습니다.\n다시 시도해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            " 가입 되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val sharedPreferences = getSharedPreferences("MY_APP_PREFERENCES", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("NAME", nickName)
                        editor.putString("ID", newId)
                        editor.putString("PASSWORD", newPassword)
                        editor.putString("CATEGORY", category)
                        editor.apply()

                        finish()
                    }
                }
            })
        }
    }
}