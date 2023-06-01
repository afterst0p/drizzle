package com.example.exericse

import android.app.Application
import android.content.Context
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI

class Cookie: Application() {
    val cookieManager = CookieManager()

    fun getCookie(url: String): List<HttpCookie> {
        return cookieManager.cookieStore.get(URI.create(url))
    }
    fun saveCookie(context: Context, url: String, cookie: HttpCookie) {
        val sharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(url, cookie.toString())
        editor.commit()
        println("cookie save")
    }

    fun loadCookie(context: Context, url: String): HttpCookie? {
        println("cookie load")
        val sharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE)
        val cookieString = sharedPreferences.getString(url, null)
        return cookieString?.let { HttpCookie.parse(it).firstOrNull() }
    }
}