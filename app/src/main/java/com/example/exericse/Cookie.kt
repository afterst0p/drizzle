package com.example.exericse

import android.app.Application
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI

class Cookie: Application() {
    val cookieManager = CookieManager()

    fun getCookie(url: String): List<HttpCookie> {
        return cookieManager.cookieStore.get(URI.create(url))
    }
}