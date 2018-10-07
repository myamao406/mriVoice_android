package jp.mediasoken.mrivoice

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
//import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_voice_check.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import com.owncloud.android.lib.common.OwnCloudClient
import com.owncloud.android.lib.common.OwnCloudClientFactory
import com.owncloud.android.lib.common.authentication.OwnCloudCredentialsFactory
import com.owncloud.android.lib.resources.files.FileUtils
import okhttp3.*
import java.io.IOException

class VoiceCheckActivity : AppCompatActivity() {

    var mBrowser: MediaBrowserCompat? = null
    var mController: MediaControllerCompat? = null
//    private var mStorageRef = FirebaseStorage.getInstance().reference

    private val mLOGTAG = VoiceCheckActivity::class.java.canonicalName

    private var mClient: OwnCloudClient? = null
    private var mOkHttpClient: OkHttpClient? = null
    private var mCredentials: String? = null

//    private val NODE_VERSION = "version"
    private val mWEBDAVPATH40 = "remote.php/webdav/音声収録/"
//    private val mWEBDAVPATH40 = "remote.php/webdav/Documents/"
//    private val NEW_WEBDAV_PATH = "/remote.php/dav/files/"
    private val mOCTOTALLENGTHHEADER = "OC-Total-Length"
    private val mOCXOCMTIMEHEADER = "X-OC-Mtime"
    private val mAUTHORIZATIONHEADER = "Authorization"
    private val mUSERAGENTHEADER = "User-Agent"
    private val mCONTENTTYPEHEADER = "Content-Type"
    private val mUSERAGENTVALUE = "Mozilla/5.0 (Android) ownCloud-android/2.7.0"
    private val mCONTENTTYPEVALUE = "multipart/form-data"

    private var filePath = Environment.getExternalStorageDirectory().path + "/sample.wav"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_check)

        val serverUri = Uri.parse(getString(R.string.server_base_url))
        mClient = OwnCloudClientFactory.createOwnCloudClient(serverUri, this, true)
        mClient?.credentials = OwnCloudCredentialsFactory.newBasicCredentials(getString(R.string.username), getString(R.string.ownPassword))
        mOkHttpClient = OkHttpClient()
        mCredentials = Credentials.basic(getString(R.string.username), getString(R.string.ownPassword))
        Log.d(mLOGTAG, "mCredentials:$mCredentials")


//        okButton.setOnClickListener { saveWavFile() }
        okButton.setOnClickListener { startUpload() }

        redoButton.setOnClickListener{ redo() }

    }

    override fun onStart() {
        super.onStart()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //シークする
                mController?.transportControls?.seekTo(seekBar.progress.toLong())
            }
        })



        //サービスは開始しておく
        //Activity破棄と同時にServiceも停止して良いならこれは不要
        startService(Intent(this, MusicService::class.java))

        //MediaBrowserを初期化
        mBrowser = MediaBrowserCompat(this, ComponentName(this, MusicService::class.java), connectionCallback, null)

        //接続(サービスをバインド)
        mBrowser?.connect()

//        play(filePath)
        mController?.transportControls?.play()

    }

/*
    private fun saveWavFile() {
        showProgress(true)
        val file = Uri.fromFile(File(fileURL.filePath))
        val df = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.JAPAN)
        val date = Date()
        val fileNName = "wav/" + currentUser.uid + "_" + df.format(date) + ".wav"
        val riversRef = mStorageRef.child(fileNName)

        riversRef.putFile(file)
                .addOnSuccessListener {
                    // Get a URL to the uploaded content
                    val intent = Intent(this@VoiceCheckActivity, VoiceListActivity::class.java)
                    //アクティビティを起動。
                    startActivity(intent)
                    showProgress(false)
                }

                .addOnFailureListener{
                    // Handle unsuccessful uploads
                    // ...
                    Log.d("FileUpLoad", "unsuccessful")
                    showProgress(false)
                }

    }
*/
    private fun redo() {
        stopService(Intent(this, MusicService::class.java))
        mBrowser?.disconnect()
        val intent = Intent(this@VoiceCheckActivity, VoiceRecPreparationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //アクティビティを起動。
        startActivity(intent)
        finish()
    }

    //接続時に呼び出されるコールバック
    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {

            try {
                //接続が完了するとSessionTokenが取得できるので
                //それを利用してMediaControllerを作成
                mController = MediaControllerCompat(this@VoiceCheckActivity,mBrowser!!.sessionToken)
                //サービスから送られてくるプレイヤーの状態や曲の情報が変更された際のコールバックを設定
                mController?.registerCallback(controllerCallback)

                //既に再生中だった場合コールバックを自ら呼び出してUIを更新
                if (mController?.playbackState != null && mController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                    controllerCallback.onMetadataChanged(mController!!.metadata)
                    controllerCallback.onPlaybackStateChanged(mController!!.playbackState)
                }


            } catch (ex: RemoteException) {
                ex.printStackTrace()
                Toast.makeText(this@VoiceCheckActivity, ex.message, Toast.LENGTH_LONG).show()
            }

            //サービスから再生可能な曲のリストを取得
            mBrowser?.subscribe(mBrowser!!.root, subscriptionCallback)
        }
    }

    //Subscribeした際に呼び出されるコールバック
    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
            //既に再生中でなければ初めの曲を再生をリクエスト
            if (mController!!.playbackState == null)
                play(children[0].mediaId)
        }
    }

    //MediaControllerのコールバック
    private val controllerCallback = object : MediaControllerCompat.Callback() {
        //再生中の曲の情報が変更された際に呼び出される
        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            textView_title.text = voiceText.title
            imageView.setImageBitmap(metadata.description.iconBitmap)
            textView_duration.text = long2TimeString(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
            Log.d("VoiceCheckActivity", textView_duration.text.toString())
            seekBar.max = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
        }

        //プレイヤーの状態が変更された時に呼び出される
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            //プレイヤーの状態によってボタンの挙動とアイコンを変更する
            if (state.state == PlaybackStateCompat.STATE_PLAYING) {
                button_play.setOnClickListener { mController?.transportControls?.pause() }
                button_play.setImageResource(R.drawable.exo_controls_pause)
            } else {
                button_play.setOnClickListener { mController?.transportControls?.play() }
                button_play.setImageResource(R.drawable.exo_controls_play)
            }
            textView_position.text = long2TimeString(state.position)
            seekBar.progress = state.position.toInt()
        }
    }

    private fun play(id: String?) {
        //MediaControllerからサービスへ操作を要求するためのTransportControlを取得する
        //playFromMediaIdを呼び出すと、サービス側のMediaSessionのコールバック内のonPlayFromMediaIdが呼ばれる
        mController?.transportControls?.playFromMediaId(id, null)
    }

    public override fun onDestroy() {
        super.onDestroy()
        mBrowser?.disconnect()
        if (mController?.playbackState?.state != PlaybackStateCompat.STATE_PLAYING)
            stopService(Intent(this, MusicService::class.java))
    }

    //Long値をm:ssの形式の文字列にする
    private fun long2TimeString(src: Long): String {
        val mm = (src / 1000 / 60).toString()
        var ss = (src / 1000 % 60).toString()

        //秒は常に二桁じゃないと変
        if (ss.length == 1) ss = "0$ss"

        return "$mm:$ss"
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            check_form.visibility = if (show) View.GONE else View.VISIBLE
            check_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            check_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            save_progress.visibility = if (show) View.VISIBLE else View.GONE
            save_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            save_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            save_progress.visibility = if (show) View.VISIBLE else View.GONE
//            check_form.visibility = if (show) View.GONE else View.VISIBLE
//        }
    }

    private fun startUpload() {

        val df = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.JAPAN)
        val date = Date()
        val contentID = String.format("%08d", voiceText.vID)
        val ageID = String.format("%02d", voiceText.Age)
        val genderID = String.format("%02d", voiceText.Gender)
        val fileNName = "/" + currentUser.uid + "_" + contentID + "_" + ageID + "_" + genderID + "_f_jp_an_" + df.format(date) + ".wav"

        val file1 = File(filePath)
        val fileToUpload = File(Environment.getExternalStorageDirectory().path + fileNName)
        try {
            if (file1.renameTo(fileToUpload)) {
                println("移動成功")
            } else {
                println("移動失敗")
                return
            }
        } catch (e: SecurityException) {
            println("例外が発生しました。")
            println(e)
            return
        } catch (e: NullPointerException) {
            println("例外が発生しました。")
            println(e)
            return
        }


        showProgress(true)
        val remotePath = FileUtils.PATH_SEPARATOR + fileToUpload.name
        val mimeType = getString(R.string.file_mimetype)

        val mediaType = MediaType.parse(mimeType)

        // Get the last modification date of the file from the file system
        val timeStampLong = fileToUpload.lastModified() / 1000
        val timeStamp = timeStampLong.toString()

        if (!validServerAddress()) {
            fileToUpload.delete()
            showProgress(false)
            return
        }

        val requestBody = RequestBody.create(mediaType, fileToUpload)

        val request = Request.Builder()
                .url(getString(R.string.server_base_url) + mWEBDAVPATH40 + voiceText.projectID + remotePath)
                .addHeader(mAUTHORIZATIONHEADER, mCredentials!!)
                .addHeader(mUSERAGENTHEADER, mUSERAGENTVALUE)
                .addHeader(mCONTENTTYPEHEADER, mCONTENTTYPEVALUE)
                .addHeader(mOCTOTALLENGTHHEADER, fileToUpload.length().toString())
                .addHeader(mOCXOCMTIMEHEADER, timeStamp)
                .put(requestBody)
                .build()
        mOkHttpClient?.newCall(request)?.enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                if (!response.isSuccessful) {
                    fileToUpload.delete()
                    hideProgress()
                    throw IOException("Unexpected code $response")
                } else { // Successful response
                    mBrowser?.disconnect()
                    val intent = Intent(this@VoiceCheckActivity, VoiceListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    //アクティビティを起動。
                    startActivity(intent)
                    fileToUpload.delete()
                    hideProgress()
                    finish()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("VoiceCheckActivity", "Something was wrong: " + e.toString())
                fileToUpload.delete()
                hideProgress()
            }
        })
    }

    private fun validServerAddress(): Boolean {

        val serverAddress = getString(R.string.server_base_url)

        if (serverAddress == "" || !serverAddress.contains("http://") && !serverAddress.contains("https://")) {
            showToastMessage("Introduce a proper server address with http/https")
            return false
        }
        return true
    }

    private fun showToastMessage(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)

        toast.setGravity(Gravity.CENTER, 0, 0)

        toast.show()
    }
    private fun hideProgress() {
        runOnUiThread { showProgress(false) }
    }

}
