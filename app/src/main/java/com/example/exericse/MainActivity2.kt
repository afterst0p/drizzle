package com.example.exericse

import Client
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class MainActivity2 : ComponentActivity() {
    private lateinit var mSocket :Socket
    private lateinit var chatScrollView: ScrollView
    private lateinit var chatLinearLayout: LinearLayout

    private lateinit var userNickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectToServer()

        setContentView(R.layout.fragment_chat)
        val sendButton = findViewById<Button>(R.id.sendButton)
        // 닉네임 가져오기
        val textView = findViewById<TextView>(R.id.textView)
        //상단 주제 표시
        val cookie = Cookie()
        val client = Client(cookie);
        client.getTopic({ result ->
            println(result)
            if (result != null) {
                runOnUiThread {
                    textView.text = result
                }
            }
        }, this)
        client.getUserInfo(this@MainActivity2) { userData ->
            if (userData != null) {
                userNickname = userData.nickName

            }
        }

        // 메시지 보내기
        sendButton.setOnClickListener{
            val textmsg = findViewById<EditText>(R.id.editText)
            val sendMessage = textmsg.text
            val sendMsg = JSONObject()

            if( sendMessage.toString().isNotEmpty()) {
                sendMsg.put("roomName", "chatRoom1")
                sendMsg.put("sendFrom", userNickname)
                sendMsg.put("chat", sendMessage)

                mSocket.emit("send", sendMsg)
            }
            textmsg.setText("")

            chatScrollView.post{
                chatScrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }

        chatScrollView = findViewById<ScrollView>(R.id.chattingScroll)
        chatLinearLayout = findViewById<LinearLayout>(R.id.chatLayout)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.emit("leave", "chatRoom1")
        mSocket.disconnect()
    }

    private fun connectToServer() {
        val options =  IO.Options()
        options.forceNew = true

        val serverUrl = "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app"

        mSocket = IO.socket(serverUrl, options)
        setupSocketListeners()

        mSocket.connect()
    }

    private fun setupSocketListeners() {
            mSocket.on(Socket.EVENT_CONNECT) {
                mSocket.emit("join", "chatRoom1")
            }

            mSocket.on(Socket.EVENT_DISCONNECT) {
                Log.d("연결", "종료")
            }

            mSocket.on("broadcast", onChat)
    }

    private val onChat = Emitter.Listener { args ->
        val jsonText = args[0] as JSONObject

        runOnUiThread{
            val inflater = LayoutInflater.from(this)
            var chatLayout = inflater.inflate(R.layout.item_your_chat, null)

            if(jsonText.get("sendFrom").toString() == userNickname){
                chatLayout =inflater.inflate(R.layout.item_my_chat, null)
            } else {
                chatLayout =inflater.inflate(R.layout.item_your_chat, null)
            }

            chatLayout.findViewById<TextView>(R.id.chat_Text).text = jsonText.get("chat").toString()
            chatLayout.findViewById<TextView>(R.id.chat_You_Name).text = jsonText.get("sendFrom").toString()
            chatLayout.findViewById<TextView>(R.id.chat_Time).text = jsonText.get("date").toString()



            chatLinearLayout.addView(chatLayout)

            chatScrollView.post{
                chatScrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }

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
        }
        return super.onOptionsItemSelected(item)
    }

}
