import android.content.Context
import com.example.exericse.Cookie
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.CookieManager


class Client(private val cookie: Cookie) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val client = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookie.cookieManager))
        .build()
    private var result = ""
    data class UserData (
        val nickName: String,
        val id: String,
        val category: String
    )
    data class  ContentData(
        val id: Long,
        val title: String,
        val content: String,
        val quiz: String,
        val selection1: String,
        val selection2: String,
        val selection3: String,
        val selection4: String,
        val answer: Int
    )

    fun loginCheck( onComplete: (String) -> Unit, context: Context) {
        val loadedCookie = cookie.loadCookie(context, "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
        val request = Request.Builder()
            .url("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app/loginCheck")
            .addHeader("Cookie", loadedCookie.toString())
            .get()
            .build()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    result = responseData ?: ""
                } else {
                    // 오류 처리
                    result = "loginCheck error"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val cookies = cookie.getCookie("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
            cookies.forEach { httpCookie ->
                cookie.saveCookie(
                    context,
                    "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app",
                    httpCookie
                )
            }
            onComplete(result)
        }
    }

    fun login(onComplete: (String) -> Unit, context: Context,  id: String, password: String){
        val requestBody = FormBody.Builder()
            .add("id", id)
            .add("pw", password)
            .build()
        val request = Request.Builder()
            .url("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app/login")
            .post(requestBody)
            .build()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    result = responseData ?: ""
                    // 응답 데이터 처리
                } else {
                    result = "login error"
                    // 오류 처리
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val cookies = cookie.getCookie("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
            cookies.forEach { httpCookie ->
                cookie.saveCookie(
                    context,
                    "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app",
                    httpCookie
                )
            }
            onComplete(result)
        }
    }
    fun signUp(nickName: String, newId: String, newPassword: String, category: String){
        val requestBody = FormBody.Builder()
            .add("nickName", nickName)
            .add("id", newId)
            .add("pw", newPassword)
            .add("category", category)
            .build()

        val request = Request.Builder()
            .url("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app/signUp")
            .post(requestBody)
            .build()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    println(responseData)
                    // 응답 데이터 처리
                } else {
                    println("error")
                    // 오류 처리
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun logout(context: Context){
        val loadedCookie = cookie.loadCookie(context, "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
        val request = Request.Builder()
            .addHeader("Cookie", loadedCookie.toString())
            .url("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app/logout")
            .get()
            .build()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    result = responseData ?: ""
                    println(result)
                } else {
                    // 오류 처리
                    result = "logout error"
                    println(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val cookies = cookie.getCookie("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
        cookies.forEach { httpCookie ->
            cookie.saveCookie(
                context,
                "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app",
                httpCookie
            )
        }
    }
    fun getUserInfo(context: Context, onComplete: (UserData?) -> Unit) {
        var readData: UserData? = null

        val loadedCookie = cookie.loadCookie(context, "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
        val request = Request.Builder()
            .addHeader("Cookie", loadedCookie.toString())
            .url("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app/userInfo")
            .get()
            .build()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val gson = Gson()
                    readData = gson.fromJson(responseBody, UserData::class.java)
                } else {
                    println("컨텐츠 불러오기 Error: ${response.code} ${response.message}")
                    throw Exception("getUserInfo() failed")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Exception: ${e.message}")
                }
            } finally {
                onComplete(readData)
            }
        }
    }

    fun getContent(context: Context, onComplete: (ContentData?) -> Unit, category: String) {
        var readData: ContentData? = null

        val loadedCookie = cookie.loadCookie(context, "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
        val request = Request.Builder()
            .addHeader("Cookie", loadedCookie.toString())
            .url("https://port-0-drizzling-backend-4c7jj2blhhwli58.sel4.cloudtype.app/quiz/get/" + category)
            .build()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val gson = Gson()
                    readData = gson.fromJson(responseBody, ContentData::class.java)
                } else {
                    println("컨텐츠 불러오기 Error: ${response.code} ${response.message}")
                    throw Exception("getContent() failed")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Exception: ${e.message}")
                }
            } finally {
                onComplete(readData)
            }
        }
    }

    fun checkLearningStatus(context: Context, onComplete: (Boolean?) -> Unit) {
        var learningStatus: Boolean? = null

        val loadedCookie = cookie.loadCookie(context, "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
        val request = Request.Builder()
            .addHeader("Cookie", loadedCookie.toString())
            .url("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app/reportCheck")
            .get()
            .build()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    learningStatus = responseBody.toBoolean()
                    println("learning status: " + learningStatus.toString())
                } else {
                    println("컨텐츠 학습 여부 확인 Error: ${response.code} ${response.message}")
                    throw Exception("checkLearningStatus() failed")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Exception: ${e.message}")
                }
            } finally {
                onComplete(learningStatus)
            }
        }
    }

    fun leaningComplete(context: Context, onComplete: (Boolean) -> Unit) {
        var sumbitSuccess: Boolean = false

        val loadedCookie = cookie.loadCookie(context, "https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app")
        val requestBody = JSONObject().apply {
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .addHeader("Cookie", loadedCookie.toString())
            .url("https://port-0-softwareengineering-e9btb72mlh4lnrto.sel4.cloudtype.app/reportCorrect")
            .put(requestBody)
            .build()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    result = responseBody.toString()
                    println(result)

                    if (result.equals("update success"))
                        sumbitSuccess = true
                } else {
                    println("학습 완료 등록 Error: ${response.code} ${response.message}")
                    throw Exception("learningComplete() failed")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Exception: ${e.message}")
                }
            } finally {
                onComplete(sumbitSuccess)
            }
        }
    }
}
