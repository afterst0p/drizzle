package com.example.exericse

import Client
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout


class TodaysContentActivity : AppCompatActivity() {
    private var backPressedOnce = false

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

        val cookie = Cookie()
        val client = Client(cookie);

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

                client.leaningComplete(this@TodaysContentActivity, { success ->
                    runOnUiThread {
                        if (success == true) {
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
                        } else {
                            Toast.makeText(
                                this@TodaysContentActivity,
                                "학습 상태 등록에 실패했습니다.\n다시 제출해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            } else {
                Toast.makeText(
                    this@TodaysContentActivity,
                    "오답입니다.\n컨텐츠를 다시 한번 읽어보시고 문제를 풀어주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                println("Wrong Answer")
            }
        }

        // 유저 정보 불러오기
        client.getUserInfo(this@TodaysContentActivity) { userData ->
            runOnUiThread {
                if (userData == null) {
                    Toast.makeText(
                        this@TodaysContentActivity,
                        "유저 정보 불러오기에 실패하였습니다.\n앱을 재시작 해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    userCategory = userData.category
                    println(userCategory)

                    // 컨텐츠 정보 불러오기
                    client.getContent(this@TodaysContentActivity, { contentData ->
                        runOnUiThread {
                            if (contentData == null) {
                                Toast.makeText(
                                    this@TodaysContentActivity,
                                    "컨텐츠 불러오기에 실패하였습니다.\n앱을 재시작 해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewTitle.text = contentData.title
                                viewContent.text = contentData.content
                                viewQuiz.text = contentData.quiz
                                viewSelection1.text = contentData.selection1
                                viewSelection2.text = contentData.selection2
                                viewSelection3.text = contentData.selection3
                                viewSelection4.text = contentData.selection4
                                quizAnswer = contentData.answer

                                // 사용자 컨텐츠 학습 여부 확인
                                client.checkLearningStatus(
                                    this@TodaysContentActivity,
                                    { learningStatus ->
                                        runOnUiThread {
                                            if (learningStatus == null) {
                                                Toast.makeText(
                                                    this@TodaysContentActivity,
                                                    " 학습 상태 불러오기에 실패하였습니다.\n앱을 재시작 해주세요.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else if (learningStatus == true) {
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

                                                viewContentConstraint.alpha = 0f
                                                viewContentConstraint.visibility =
                                                    ConstraintLayout.VISIBLE
                                                viewContentConstraint.animate().alpha(1f).duration =
                                                    150
                                            } else {
                                                viewContentConstraint.alpha = 0f
                                                viewContentConstraint.visibility =
                                                    ConstraintLayout.VISIBLE
                                                viewContentConstraint.animate().alpha(1f).duration =
                                                    150
                                            }
                                        }
                                    }) // 사용자 컨텐츠 학습 여부 확인
                            }
                        }
                    }, userCategory) // 컨텐츠 정보 불러오기
                }
            }
        } // 유저 정보 불러오기
    }

    override fun onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed()
            finishAffinity()
        } else {
            backPressedOnce = true
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }

        Handler().postDelayed({
            backPressedOnce = false
        }, 2000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_todays_content, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_chat -> {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            }
            R.id.action_push -> {
                val intent = Intent(this, PushListActivity::class.java)
                startActivity(intent)
            }
            R.id.action_logout -> {
                val cookie = Cookie()
                val client = Client(cookie);
                client.logout(this@TodaysContentActivity)

                // 컨텐츠 갱신 알림 해제
                val updateNotification = UpdateNotification(this)
                updateNotification.cancel()

                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}