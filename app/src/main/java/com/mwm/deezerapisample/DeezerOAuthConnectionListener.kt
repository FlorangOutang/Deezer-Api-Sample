package com.mwm.deezerapisample

interface DeezerOAuthConnectionListener {
    fun onAuthConnectionSuccess(message: String)
    fun onAuthConnectionFailure(exception: Exception)
}