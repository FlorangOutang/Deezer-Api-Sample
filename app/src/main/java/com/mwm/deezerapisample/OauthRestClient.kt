package com.mwm.deezerapisample

import com.squareup.okhttp.Callback
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

class OauthRestClient {
    private val client: OkHttpClient = OkHttpClient()
    fun authenticate(
        appId: String,
        clientSecret: String,
        code: String,
        callback: Callback
    ) {
        val httpBuilder: HttpUrl.Builder =
            HttpUrl.parse("https://connect.deezer.com/oauth/access_token.php").newBuilder()
        val request: Request = Request.Builder()
            .url(
                httpBuilder
                    .addQueryParameter("app_id", appId)
                    .addQueryParameter("client_secret", clientSecret)
                    .addQueryParameter("code", code)
                    .build()
            )
            .build()

        client.newCall(request).enqueue(callback)
    }
}