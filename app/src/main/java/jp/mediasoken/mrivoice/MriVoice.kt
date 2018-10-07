package jp.mediasoken.mrivoice

import android.app.Application
import android.content.Context

class MriVoice: Application() {
    override fun onCreate() {
        super.onCreate()
        MriVoice.appContext = getApplicationContext()
    }
    companion object {
        lateinit var appContext: Context
    }
}