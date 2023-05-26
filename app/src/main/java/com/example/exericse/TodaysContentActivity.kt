package com.example.exericse

import Client
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class TodaysContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todays_content)

        var userCategory: String = "카테고리"
        var quizAnswer: Int = -1
        var selectedAnswer: Int = 0

        val viewTitle = findViewById<TextView>(R.id.view_title)
        val viewContent = findViewById<TextView>(R.id.view_content)
        val viewQuiz = findViewById<TextView>(R.id.view_quiz)
        val viewSelectionGroup = findViewById<RadioGroup>(R.id.view_selection_group)
        val viewSelection1 = findViewById<RadioButton>(R.id.view_selection1)
        val viewSelection2 = findViewById<RadioButton>(R.id.view_selection2)
        val viewSelection3 = findViewById<RadioButton>(R.id.view_selection3)
        val viewSelection4 = findViewById<RadioButton>(R.id.view_selection4)
        val viewCheckAnswerButton = findViewById<Button>(R.id.view_check_answer)
        val viewContentConstraint = findViewById<ConstraintLayout>(R.id.view_content_constraint)

        val fadeDuration = 1000L // 페이드인 1초
        val fadeInAnimation = AlphaAnimation(0f, 1f).apply {
            duration = fadeDuration
        }
        viewContentConstraint.startAnimation(fadeInAnimation)

        val cookieManager = (applicationContext as Cookie).cookieManager
        val client = Client(cookieManager);

        // Quiz 라디오 버튼 Listener
        viewSelectionGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            selectedAnswer = when (selectedRadioButton) {
                viewSelection1 -> 1
                viewSelection2 -> 2
                viewSelection3 -> 3
                viewSelection4 -> 4
                else -> 0
            }
            println("Selected Number: ${quizAnswer}")
        }

        // Quiz 정답 제출 버튼 Listener
        viewCheckAnswerButton.setOnClickListener{
            if (selectedAnswer == quizAnswer) {
                Toast.makeText(
                    this@TodaysContentActivity,
                    "정답입니다!",
                    Toast.LENGTH_SHORT
                ).show()
                println("Correct Answer")

                try {
                    client.leaningComplete({success ->
                        if (success == true) {
                            runOnUiThread {
                                viewCheckAnswerButton.text = "학습 완료"
                                when (quizAnswer) {
                                    1 -> viewSelection1.isChecked = true
                                    2 -> viewSelection2.isChecked = true
                                    3 -> viewSelection3.isChecked = true
                                    4 -> viewSelection4.isChecked = true
                                }
                                viewCheckAnswerButton.isEnabled = false
                                viewSelection1.isEnabled = false
                                viewSelection2.isEnabled = false
                                viewSelection3.isEnabled = false
                                viewSelection4.isEnabled = false
                            }
                        }
                        else
                            throw Exception()
                    })
                } catch (e: Exception) {
                    Toast.makeText(
                        this@TodaysContentActivity,
                        "학습 상태 등록에 실패했습니다. 다시 제출해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@TodaysContentActivity,
                    "오답입니다.\n컨텐츠를 다시 한번 읽어보시고 문제를 풀어주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                println("Wrong Answer")
            }
        }


        try {
            // 유저 정보 불러오기
            client.getUserInfo({ readData ->
                runOnUiThread {
                    userCategory = readData.category
                    println(userCategory)

                    // 컨텐츠 정보 불러오기
                    client.getContent({ readData ->
                        runOnUiThread {
                            viewTitle.text = readData.title
                            viewContent.text = readData.content
                            viewQuiz.text = readData.quiz
                            viewSelection1.text = readData.selection1
                            viewSelection2.text = readData.selection2
                            viewSelection3.text = readData.selection3
                            viewSelection4.text = readData.selection4
                            quizAnswer = readData.answer

                            // 사용자 컨텐츠 학습 여부 확인
                            client.checkLearningStatus({ learningStatus ->
                                runOnUiThread {
                                    if (learningStatus == true) {
                                        viewCheckAnswerButton.text = "학습 완료"
                                        println(quizAnswer.toString())
                                        when (quizAnswer) {
                                            1 -> viewSelection1.isChecked = true
                                            2 -> viewSelection2.isChecked = true
                                            3 -> viewSelection3.isChecked = true
                                            4 -> viewSelection4.isChecked = true
                                        }
                                        viewCheckAnswerButton.isEnabled = false
                                        viewSelection1.isEnabled = false
                                        viewSelection2.isEnabled = false
                                        viewSelection3.isEnabled = false
                                        viewSelection4.isEnabled = false
                                    }
                                }
                            })
                        }
                    }, userCategory) // 회원 정보에서 어떻게 카테고리를 불러올까?
                }
            }) // 회원 정보에서 어떻게 카테고리를 불러올까?
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(
                    this@TodaysContentActivity,
                    "유저 정보 불러오기에 실패하였습니다. 앱을 재시작 해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}