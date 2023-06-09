package com.example.exericse

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class PushListActivity : ComponentActivity() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_list)

        val pushList = findViewById<RecyclerView>(R.id.pushList)

        val itemList = ArrayList<PushItem>()

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
                        val pushAdapter = PushListAdapter(itemList)
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
}


data class PushItem(val name: String, val userid: String)      // data info


class PushListAdapter (private val itemList: ArrayList<PushItem>) :
    RecyclerView.Adapter<PushListAdapter.PushListHolder>() {

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
            pushMsg.put("sendTo", itemList[position].userid)

            val myID = "1" // 임시로 사용한 ID    //이걸 내 id로 바꾸면 됨
            pushMsg.put("sendFrom", myID)
            //mSocket.emit("push", pushMsg)  // 소켓 연결이 되면 이대로 쓰면 됨 // mSocket : Socket
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
