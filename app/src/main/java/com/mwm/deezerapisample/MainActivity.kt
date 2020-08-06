package com.mwm.deezerapisample

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.mwm.deezerapisample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val appId = "insert_your_app_id"
    private val clientSecret = "insert_your_client_secret"
    private val redirectUri = "insert_your_redirect_uri"
    private val deezerHost = "deezer.com"

    private val oauthUri = """
        https://connect.deezer.com/oauth/auth.php/
        ?app_id=$appId
        &redirect_uri=$redirectUri
        &perms=basic_access,listening_history,offline_access
        &response_type=token
    """.trimIndent()

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        textView = binding.textView
        textView.movementMethod = ScrollingMovementMethod()
        textView.text = "Please insert your app credential in the webview configuration above"

        val webViewClient = createOauthWebViewClient(intent)
        binding.webView.webViewClient = webViewClient

        val settings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.userAgentString = "Android"

        binding.webView.loadUrl(oauthUri)

        setContentView(binding.root)
    }

    private fun createOauthWebViewClient(intent: Intent): WebViewClient? {
        val restClient = OauthRestClient()
        return DeezerOAuthWebViewClient(
            restClient,
            appId,
            clientSecret,
            redirectUri,
            deezerHost,
            createDeezerOAuthConnectionListener()
        )
    }

    private fun createDeezerOAuthConnectionListener() = object : DeezerOAuthConnectionListener {
        override fun onAuthConnectionSuccess(message: String) {
            textView.setTextColor(ResourcesCompat.getColor(resources, android.R.color.holo_green_dark, null))
            textView.text = "access token : $message"
        }

        override fun onAuthConnectionFailure(exception: Exception) {
            textView.setTextColor(ResourcesCompat.getColor(resources, android.R.color.holo_red_dark, null))
            textView.text = exception.message
        }

    }
}