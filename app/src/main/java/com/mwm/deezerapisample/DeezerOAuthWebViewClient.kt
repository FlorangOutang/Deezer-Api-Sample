package com.mwm.deezerapisample

import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.squareup.okhttp.Callback
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import com.squareup.okhttp.ResponseBody
import java.io.IOException

class DeezerOAuthWebViewClient(
    private val restClient: OauthRestClient,
    private val appId: String,
    private val clientSecret: String,
    private val redirectUri: String,
    private val deezerHost: String,
    private val listener: DeezerOAuthConnectionListener
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (!url.contains(redirectUri)) {
            return false
        }

        var accessToken: String? = null
        var expires = 0

        val redirectUri = Uri.parse(url)
        val fragment = redirectUri.fragment
        if (fragment != null) {
            try {
                accessToken = extractAccessToken(fragment)
                expires = extractExpires(fragment)
            } catch (exception: Exception) {
                Log.e(TAG, exception.message)
            }
        }

        if (accessToken != null) {
            listener.onAuthConnectionSuccess(accessToken)
        } else {
            val code =
                redirectUri.getQueryParameter("code")
            if (code != null) {
                getAccessToken(code)
            } else {
                val message = ("Error during retrieve the UserCode in params of redirect URI (found : '"
                        + url
                        + "')")
                Log.e(TAG, message)
                listener.onAuthConnectionFailure(Exception(message))
            }
        }

        return true
    }

    @Throws(Exception::class)
    private fun extractAccessToken(fragment: String?): String {
        val accessTokenStartIndex =
            fragment!!.indexOf("access_token") + "access_token".length + 1
        val accessTokenEndIndex = fragment.indexOf("&", accessTokenStartIndex)
        return fragment.substring(accessTokenStartIndex, accessTokenEndIndex)
    }

    @Throws(Exception::class)
    private fun extractExpires(fragment: String?): Int {
        val expiresStartIndex = fragment!!.indexOf("expires") + "expires".length + 1
        val expiresEndIndex = fragment.length
        return fragment.substring(expiresStartIndex, expiresEndIndex).toInt()
    }

    private fun getAccessToken(code: String) {
        restClient.authenticate(
            appId,
            clientSecret,
            code,
            object : Callback {
                override fun onFailure(request: Request?, e: IOException) {
                    Log.e(
                        TAG, "Failed to get access token."
                                + " exception -> " + e.message
                    )
                    listener.onAuthConnectionFailure(e)
                }

                override fun onResponse(response: Response) {
                    val responseBody: ResponseBody = response.body()
                    if (!response.isSuccessful) {
                        val message = ("Failed to get access token. Unsuccessful response."
                                + " code -> " + response.code()
                                + " message -> " + response.message())
                        Log.e(TAG, message)
                        listener.onAuthConnectionFailure(Exception(message))
                    }
                    var responseString: String? = null
                    try {
                        responseString = responseBody.string()
                        extractAccessToken(responseString)
                        extractExpires(responseString)
                        listener.onAuthConnectionSuccess(extractAccessToken(responseString))
                    } catch (e: Exception) {
                        val message = ("Failed to extract access token from response."
                                + " response -> " + responseString
                                + " message -> " + e.message)
                        Log.e(TAG, message)
                        listener.onAuthConnectionFailure(Exception(message))
                    }
                }
            }
        )
    }

    companion object {
        private const val TAG = "DeezrOAuthWebViewClient"
    }
}