package jp.mediasoken.mrivoice

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.ActionBar
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.TextAppearanceSpan
import android.view.MenuItem
import java.io.File
import kotlinx.android.synthetic.main.activity_voice_rec_preparation.*

class VoiceRecPreparationActivity : AppCompatActivity() {
    private var _isRecording = true
    val handler = Handler()
    var timeValue = 0
    private var stringPos = 0
    var textSize = 14.0F


    private var mRecorder: PcmAudioRecorder? = null
    private val mRcordFilePath = fileURL.filePath

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_rec_preparation)

        val redTextAppearanceSpan = TextAppearanceSpan(this, R.style.MyTextAppearance)

        val actionBar: ActionBar? = supportActionBar

        when {
            actionBar != null -> {
                actionBar.setDisplayHomeAsUpEnabled(true)
            }
        }

        mRecorder = PcmAudioRecorder.getInstanse()
        mRecorder!!.setOutputFile(mRcordFilePath)

        val sb = SpannableStringBuilder()

        sb.append(getString(R.string.textSizeChangeButtonText))

        if (stringPos < 0){
            stringPos = 0
        }
        sb.setSpan(redTextAppearanceSpan, stringPos, stringPos+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        sb.setSpan(RelativeSizeSpan(1.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(RelativeSizeSpan(2.0f), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textSizeButton.text = sb

        ageLabel.text = Constants.ageLabel[voiceText.Age]
        genderLabel.text = Constants.genderLabel[voiceText.Gender]
        titleLabel.text = voiceText.title
        createdLabel.text = voiceText.Created
        deadLineLabel.text = voiceText.Deadline
        detailText.text = voiceText.contents

        _isRecording = true
        recordButton.text = "録音"

        // 1秒ごとに処理を実行
        val runnable = object : Runnable {
            override fun run() {
                // TextViewを更新
                // ?.letを用いて、nullではない場合のみ更新
                timeToText(timeValue)?.let {
                    // timeToText(timeValue)の値がlet内ではitとして使える
                    recTimeTextView.text = it
                }
                timeValue++
                handler.postDelayed(this, 1000)
            }
        }

        textSizeButton.setOnClickListener{
            if(stringPos >= 2){
                stringPos = 0
                textSize = 14.0F
            } else {
                stringPos += 1
                textSize = 14.0F * (1.0F + stringPos*0.5F)

            }
            sb.setSpan(redTextAppearanceSpan, stringPos, stringPos+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            textSizeButton.text = sb
            detailText.textSize = textSize
        }


        recordButton.setOnClickListener{
            if (!_isRecording) {
                stopRecord()
                handler.removeCallbacks(runnable)
                //インテントオブジェクトを用意。
                val intent = Intent(this@VoiceRecPreparationActivity, VoiceCheckActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                //アクティビティを起動。R.layout.row
                startActivity(intent)

            } else {
                doRecord()
                handler.post(runnable)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 選択されたメニューのIDを取得
        val itemId = item!!.itemId
        // 選択されたメニューが[←]の場合、アクティビティを終了する。
        if (itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun doRecord() {
        recordButton.text = "停止"
        _isRecording = !_isRecording

        /* ファイルが存在する場合は削除 */
        val wavFile: File? = File(fileURL.filePath)
        if (wavFile!!.exists()){
            wavFile.delete()
        }

        mRecorder?.prepare()
        mRecorder?.start()
    }

    private fun stopRecord() {
        recordButton.text = "録音"
        _isRecording = !_isRecording

        mRecorder?.stop()
        mRecorder?.reset()
    }

    // 数値を00:00:00形式の文字列に変換する関数
    // 引数timeにはデフォルト値0を設定、返却する型はnullableなString?型
    private fun timeToText(time: Int = 0): String? {
        return when {
            time < 0 -> {
                null
            }
            time == 0 -> {
                "00:00:00"
            }
            else -> {
                val h = time / 3600
                val m = time % 3600 / 60
                val s = time % 60
                "%1$02d:%2$02d:%3$02d".format(h, m, s)
            }
        }
    }
}
