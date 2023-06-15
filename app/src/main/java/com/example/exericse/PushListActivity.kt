package com.example.exericse

import Client
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.socket.client.IO
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import io.socket.client.Socket
import io.socket.emitter.Emitter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class PushListActivity : ComponentActivity() {
    private lateinit var mSocket :Socket
    private lateinit var userNickname: String
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_list)
        val cookie = Cookie()
        val client = Client(cookie);
//        client.getUserInfo(this@PushListActivity) { userData ->
//            if (userData != null) {
//                userNickname = userData.nickName
//
//            }
//            connectToServer()
//        }
        var get:Intent = getIntent()
        userNickname = get.getStringExtra("nickName") as String
        Log.d("nickName", userNickname);
        connectToServer()
        val pushList = findViewById<RecyclerView>(R.id.pushList)

        val itemList = ArrayList<PushItem>()

//        connectToServer()

        var userDataJson : String
        try {           //유저 리스트 가져오는 부분
            val idUrl =
                "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app/memberList"
            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url(idUrl).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Http Connection", "Failure")
                }

                override fun onResponse(call: Call, response: Response) {
                    userDataJson = response.body?.string() as String
                    Log.d("Http Connection", "-----Success-----")
                    Log.d("Http Connection", response.toString())
                    Log.d("Http Connection", userDataJson)
                    Log.d("Http Connection", "----------------")


                    val userDataArr = JSONArray(userDataJson)

                    for (i in 0 until userDataArr.length()) {       //유저 수 만큼 리스트 생성
                        val userData: JSONObject = userDataArr.getJSONObject(i)
                        itemList.add( PushItem( userData.get("nickName").toString(),
                                                userData.get("id").toString() ) )
                    }

                    runOnUiThread {
                        val pushAdapter = PushListAdapter(itemList, userNickname, mSocket)
                        pushAdapter.notifyDataSetChanged()

                        pushList.adapter = pushAdapter
                    }

                }

            })
        } catch (e:Exception){
            Log.e("HTTP Connection", "Error :" + e.message)
        }

        pushList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    private fun connectToServer() {  //수정필요
        val options =  IO.Options()
        options.forceNew = true

        val serverUrl = "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app"

        mSocket = IO.socket(serverUrl, options)
        setupSocketListeners()

        mSocket.connect()
    }

    private fun setupSocketListeners() {
        mSocket.on(Socket.EVENT_CONNECT) {
            mSocket.emit("join", userNickname)
        }

        mSocket.on(Socket.EVENT_DISCONNECT) {
            Log.d("연결", "종료")
        }

        mSocket.on("push") { data ->
            showNotification(this@PushListActivity, data.get(0).toString())
        }
    }
    // 알림 표시
    fun showNotification(context: Context, message: String) {
        // 알림을 클릭했을 때 실행될 액티비티 설정
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE  // 플래그 추가
        )

        // 알림 채널 생성 (Android 8.0 이상에서 필요)
        val channelId = "channel_id"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 빌더 생성
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("알림")
            .setContentText(message + "께서 꾹 찔렀습니다.")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 알림 표시
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(0, builder.build())
    }



}


data class PushItem(val name: String, val userid: String)      // data info

class PushListAdapter (private val itemList: ArrayList<PushItem>, private val userNIckname:String, mSocket: Socket) :
    RecyclerView.Adapter<PushListAdapter.PushListHolder>() {
    private var userNickname: String = userNIckname
    private var mSocket = mSocket


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PushListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.push_list_item, parent, false)

        return PushListHolder(view)
    }

    override fun onBindViewHolder(holder: PushListHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.pushButton.setOnClickListener {      //버튼 눌렀을 때 실행 될 함수 body
            Log.d("Push Button", itemList[position].name)
            Log.d("push Button", itemList[position].userid)

            val pushMsg = JSONObject()
            val testMsg = JSONObject()
            pushMsg.put("sendTo", itemList[position].name)

            val myID = userNickname // 임시로 사용한 ID    //이걸 내 id로 바꾸면 됨
            pushMsg.put("sendFrom", myID)
            testMsg.put("sendFrom", myID)
            testMsg.put("sendTo", myID)
            mSocket.emit("push", pushMsg)  // 소켓 연결이 되면 이대로 쓰면 됨 // mSocket : Socket
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class PushListHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById<TextView>(R.id.name)
        val pushButton: Button = itemView.findViewById<Button>(R.id.button)
    }
}
