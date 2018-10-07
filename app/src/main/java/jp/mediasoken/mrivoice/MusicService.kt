package jp.mediasoken.mrivoice


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
//import android.media.AudioAttributes
//import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

import java.util.ArrayList

class MusicService : MediaBrowserServiceCompat() {
    internal val TAG = MusicService::class.java.simpleName  //ログ用タグ
    internal val ROOT_ID = "root"                           //クライアントに返すID onGetRoot / onLoadChildrenで使用

    internal var handler = Handler()                        //定期的に処理を回すためのHandler

    internal var mSession: MediaSessionCompat? = null       //主役のMediaSession
    internal var am: AudioManager? = null                   //AudioFoucsを扱うためのManager

    internal var index = 0                                  //再生中のインデックス

    internal var exoPlayer: ExoPlayer? = null               //音楽プレイヤーの実体

    internal var queueItems: MutableList<MediaSessionCompat.QueueItem> = ArrayList()    //キューに使用するリスト

    /* 録音先のパス */
    private var filePath = "file://" + Environment.getExternalStorageDirectory().path + "/"

    private val channelId = "my_service"

//    private var mAudioFocusRequest: AudioFocusRequest? = null

    //MediaSession用コールバック
    private val callback = object : MediaSessionCompat.Callback() {

        //曲のIDから再生する
        //WearやAutoのブラウジング画面から曲が選択された場合もここが呼ばれる
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            //今回はAssetsフォルダに含まれる音声ファイルを再生
            //Uriから再生する
            val dataSourceFactory = DefaultDataSourceFactory(applicationContext, Util.getUserAgent(applicationContext, "AppName"))

            val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(filePath + MusicLibrary.getMusicFilename(mediaId!!)))

            //今回は簡易的にmediaIdからインデックスを割り出す。
            for (item in queueItems)
                if (item.description.mediaId == mediaId)
                    index = item.queueId.toInt()

            exoPlayer?.prepare(mediaSource)

            mSession?.isActive = true

            onPlay()

            //MediaSessionが配信する、再生中の曲の情報を設定
            Log.d("MusicService","MusicService")
            mSession?.setMetadata( MusicLibrary.getMetadata(applicationContext, mediaId))

        }

        //再生をリクエストされたとき
        override fun onPlay() {
            //オーディオフォーカスを要求

//            val audioAttributes = AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .build()
//
//            mAudioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
//                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
//                    .setAudioAttributes(audioAttributes)
//                    .build()
//
//            if (am?.requestAudioFocus()) {
//
//            }

            if (am?.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //取得できたら再生を始める
                mSession?.isActive = true
                exoPlayer?.playWhenReady = true
            }
        }

        //一時停止をリクエストされたとき
        override fun onPause() {
            exoPlayer?.playWhenReady = false
            //オーディオフォーカスを開放
            am?.abandonAudioFocus(afChangeListener)
        }

        //停止をリクエストされたとき
        override fun onStop() {
            onPause()
            mSession?.isActive = false
            //オーディオフォーカスを開放
            am?.abandonAudioFocus(afChangeListener)
//            am?.abandonAudioFocusRequest

        }

        //シークをリクエストされたとき
        override fun onSeekTo(pos: Long) {
            exoPlayer?.seekTo(pos)
        }

        //次の曲をリクエストされたとき
        override fun onSkipToNext() {
            index++
            if (index >= queueItems.size)
            //ライブラリの最後まで再生したら
                index = 0//最初に戻す

            onPlayFromMediaId(queueItems[index].description.mediaId, null)
        }

        //前の曲をリクエストされたとき
        override fun onSkipToPrevious() {
            index--
            if (index < 0)
            //インデックスが0以下になったら
                index = queueItems.size - 1//最後の曲に移動する

            onPlayFromMediaId(queueItems[index].description.mediaId, null)
        }

        //WearやAutoでキュー内のアイテムを選択された際にも呼び出される
        override fun onSkipToQueueItem(i: Long) {
            onPlayFromMediaId(queueItems[i.toInt()].description.mediaId, null)
        }

        //Media Button Intentが飛んできた時に呼び出される
        //オーバーライド不要（今回はログを吐くだけ）
        //MediaSessionのplaybackStateのActionフラグに応じてできる操作が変わる
        override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
            val key = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            Log.d(TAG,key.toString())
            return super.onMediaButtonEvent(mediaButtonEvent)
        }
    }

    //プレイヤーのコールバック
    private val eventListener = object : Player.DefaultEventListener() {
        //プレイヤーのステータスが変化した時に呼ばれる
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            updatePlaybackState()
        }
    }


    //オーディオフォーカスのコールバック
    internal var afChangeListener: AudioManager.OnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        //フォーカスを完全に失ったら
        Log.d(TAG, "In onAudioFocusChange (${this.toString().substringAfterLast("@")}), focus changed to = $focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.d(TAG, "AUDIOFOCUS_LOSS")
                //止める
//                mSession!!.controller.transportControls.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {             //一時的なフォーカスロスト
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
                //止める
                mSession!!.controller.transportControls.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {    //通知音とかによるフォーカスロスト（ボリュームを下げて再生し続けるべき）
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                //本来なら音量を一時的に下げるべきだが何もしない
            }
            AudioManager.AUDIOFOCUS_GAIN -> {                       //フォーカスを再度得た場合
                Log.d(TAG, "AUDIOFOCUS_GAIN")
                //再生
                mSession!!.controller.transportControls.play()
            }

        }
    }

    override fun onCreate() {
        super.onCreate()

        //AudioManagerを取得
        am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //MediaSessionを初期化
        mSession = MediaSessionCompat(applicationContext, TAG)
        //このMediaSessionが提供する機能を設定
        mSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or     //ヘッドフォン等のボタンを扱う

                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS or               //キュー系のコマンドの使用をサポート

                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)             //再生、停止、スキップ等のコントロールを提供

        //クライアントからの操作に応じるコールバックを設定
        mSession?.setCallback(callback)

        //MediaBrowserServiceにSessionTokenを設定
        sessionToken = mSession?.sessionToken

        //Media Sessionのメタデータや、プレイヤーのステータスが更新されたタイミングで
        //通知の作成/更新をする
        mSession!!.controller.registerCallback(object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                createNotification()
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                createNotification()
            }
        })

        //キューにアイテムを追加
        for ((index,media) in MusicLibrary.mediaItems.withIndex()) {
            queueItems.add(MediaSessionCompat.QueueItem(media.description, index.toLong()))
        }
        mSession?.setQueue(queueItems)//WearやAutoにキューが表示される


        //exoPlayerの初期化
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, DefaultTrackSelector())
        //プレイヤーのイベントリスナーを設定
        exoPlayer?.addListener(eventListener)

        handler = Handler()
        //500msごとに再生情報を更新
        handler.postDelayed(object : Runnable {
            override fun run() {
                //再生中にアップデート
                if (exoPlayer!!.playbackState == Player.STATE_READY && exoPlayer!!.playWhenReady)
                    updatePlaybackState()

                //再度実行
                handler.postDelayed(this, 500)
            }
        }, 500)
    }

    //クライアント接続時に呼び出される
    //パッケージ名などから接続するかどうかを決定する
    //任意の文字列を返すと接続許可
    //nullで接続拒否
    //今回は全ての接続を許可
    override fun onGetRoot(clientPackageName: String,
                           clientUid: Int,
                           rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? {
        Log.d(TAG, "Connected from pkg:$clientPackageName uid:$clientUid")
        return MediaBrowserServiceCompat.BrowserRoot(ROOT_ID, null)
    }

    //クライアント側がsubscribeを呼び出すと呼び出される
    //音楽ライブラリの内容を返す
    //WearやAutoで表示される曲のリストにも使われる
    //デフォルトでonGetRootで返した文字列がparentMediaIdに渡される
    //ブラウザ画面で子要素を持っているMediaItemを選択した際にもそのIdが渡される
    override fun onLoadChildren(
            parentMediaId: String,
            result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>) {

        if (parentMediaId == ROOT_ID)
        //曲のリストをクライアントに送信
            result.sendResult(MusicLibrary.mediaItems)
        else
        //今回はROOT_ID以外は無効
            result.sendResult(ArrayList())
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        mSession?.isActive = false
        mSession?.release()
        exoPlayer?.stop()
        exoPlayer?.release()
    }

    //MediaSessionが配信する、現在のプレイヤーの状態を設定する
    //ここには再生位置の情報も含まれるので定期的に更新する
    private fun updatePlaybackState() {
        var state = PlaybackStateCompat.STATE_NONE
        //プレイヤーの状態からふさわしいMediaSessionのステータスを設定する
        when (exoPlayer?.playbackState) {
            Player.STATE_IDLE -> state = PlaybackStateCompat.STATE_NONE
            Player.STATE_BUFFERING -> state = PlaybackStateCompat.STATE_BUFFERING
            Player.STATE_READY -> state = when {
                (exoPlayer!!.playWhenReady) -> PlaybackStateCompat.STATE_PLAYING
                else -> PlaybackStateCompat.STATE_PAUSED
            }
            Player.STATE_ENDED -> state = PlaybackStateCompat.STATE_STOPPED
        }

        //プレイヤーの情報、現在の再生位置などを設定する
        //また、MeidaButtonIntentでできる操作を設定する
        mSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_STOP)
                .setState(state, exoPlayer!!.currentPosition, exoPlayer!!.playbackParameters.speed)
                .build())
    }

    //通知を作成、サービスをForegroundにする
    private fun createNotification() {

        val controller = mSession?.controller
        val mediaMetadata = controller?.metadata

        if (mediaMetadata == null && !mSession!!.isActive) return

        val description = mediaMetadata!!.description

        val builder = NotificationCompat.Builder(applicationContext,channelId)

        builder
                //現在の曲の情報を設定
                .setContentTitle(description.title)
                .setContentText(description.subtitle)
                .setSubText(description.description)
                .setLargeIcon(description.iconBitmap)

                // 通知をクリックしたときのインテントを設定
                .setContentIntent(createContentIntent())

                // 通知がスワイプして消された際のインテントを設定
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP))

                // 通知の範囲をpublicにしてロック画面に表示されるようにする
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .setSmallIcon(R.drawable.exo_controls_play)
                //通知の領域に使う色を設定
                //Androidのバージョンによってスタイルが変わり、色が適用されない場合も多い
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))

                // Media Styleを利用する
                .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mSession?.sessionToken)
                        //通知を小さくたたんだ時に表示されるコントロールのインデックスを設定
                        .setShowActionsInCompactView(1))

        // Android4.4以前は通知をスワイプで消せないので
        //キャンセルボタンを表示することで対処
        //今回はminSDKが21なので必要ない
        //.setShowCancelButton(true)
        //.setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
        //        PlaybackStateCompat.ACTION_STOP)));


        //通知のコントロールの設定
//        builder.addAction(NotificationCompat.Action(
//                R.drawable.exo_controls_previous, "prev",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))


        //プレイヤーの状態で再生、一時停止のボタンを設定
        if (controller.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            builder.addAction(NotificationCompat.Action(
                    R.drawable.exo_controls_pause, "pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                            PlaybackStateCompat.ACTION_PAUSE)))
        } else {
            builder.addAction(NotificationCompat.Action(
                    R.drawable.exo_controls_play, "play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                            PlaybackStateCompat.ACTION_PLAY)))
        }


//        builder.addAction(NotificationCompat.Action(
//                R.drawable.exo_controls_next, "next",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))

//        startForeground(1, builder.build())

        //再生中以外ではスワイプで通知を消せるようにする
        if (controller.playbackState.state != PlaybackStateCompat.STATE_PLAYING)
            stopForeground(false)
    }


    //通知をクリックしてActivityを開くインテントを作成
    private fun createContentIntent(): PendingIntent {
        val openUI = Intent(this, VoiceCheckActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
                this, 1, openUI, PendingIntent.FLAG_CANCEL_CURRENT)
    }
}
