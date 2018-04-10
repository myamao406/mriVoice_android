package jp.mediasoken.mrivoice

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class VoiceRecPreparationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_rec_preparation)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
