package jp.mediasoken.mrivoice

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
//import android.os.Environment
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import java.io.FileDescriptor
import java.io.FileInputStream

import java.util.ArrayList
import java.util.HashMap
import java.util.TreeMap
import java.util.concurrent.TimeUnit

class MusicLibrary {

    companion object{
        private val music = TreeMap<String, MediaMetadataCompat>()
        private val albumRes = HashMap<String, Int>()
        private val musicFileName = HashMap<String, String>()
        private var mp: MediaPlayer? = null
        private var fs: FileInputStream? = null
        private var fds: FileDescriptor? = null
        /* 録音先のパス */
//        private var filePath = Environment.getExternalStorageDirectory().path + "/sample.wav"
        private var filePath = fileURL.filePath
        val root: String
            get() = "root"

        val mediaItems: List<MediaBrowserCompat.MediaItem>
            get() {
                val result = ArrayList<MediaBrowserCompat.MediaItem>()
                for (metadata in music.values) {
                    result.add(
                            MediaBrowserCompat.MediaItem(
                                    metadata.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
                }
                return result
            }

        init {
            createMediaMetadataCompat(
                    "Jazz_In_Paris",
                    "Jazz in Paris",
                    "Media Right Productions",
                    "Jazz & Blues",
                    "Jazz",
                    103,
                    TimeUnit.SECONDS,
                    "sample.wav",
                    R.drawable.album_jazz_blues,
                    "album_jazz_blues")
        }

        private fun getAlbumArtUri(albumArtResName: String): String {
            return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    BuildConfig.APPLICATION_ID + "/drawable/" + albumArtResName
        }

        fun getMusicFilename(mediaId: String): String? {
            return if (musicFileName.containsKey(mediaId)) musicFileName[mediaId] else null
        }

        private fun getAlbumRes(mediaId: String): Int {
            return if (albumRes.containsKey(mediaId)) albumRes[mediaId]!! else 0
        }

        fun getAlbumBitmap(context: Context, mediaId: String): Bitmap {
            return BitmapFactory.decodeResource(context.resources,
                    getAlbumRes(mediaId))
        }


        private fun getDuration(audioFile:String):Long {
            mp = MediaPlayer()
            fs = FileInputStream(audioFile)
            fds = fs?.fd
            mp?.setDataSource(fds)
            mp?.prepare()
            val length = mp?.duration
            mp?.release()
            return (length!!/1000).toLong()
        }

        fun getMetadata(context: Context, mediaId: String): MediaMetadataCompat {
            createMediaMetadataCompat(
                    "Jazz_In_Paris",
                    "Jazz in Paris",
                    "Media Right Productions",
                    "Jazz & Blues",
                    "Jazz",
                    103,
                    TimeUnit.SECONDS,
                    "sample.wav",
                    R.drawable.album_jazz_blues,
                    "album_jazz_blues")

            val metadataWithoutBitmap = music[mediaId]
            val albumArt = getAlbumBitmap(context, mediaId)

            // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
            // We don't set it initially on all queueItems so that they don't take unnecessary memory.
            val builder = MediaMetadataCompat.Builder()
            for (key in arrayOf(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                    MediaMetadataCompat.METADATA_KEY_ALBUM,
                    MediaMetadataCompat.METADATA_KEY_ARTIST,
                    MediaMetadataCompat.METADATA_KEY_GENRE,
                    MediaMetadataCompat.METADATA_KEY_TITLE)) {
                builder.putString(key, metadataWithoutBitmap!!.getString(key))
            }
            builder.putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    metadataWithoutBitmap!!.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
            return builder.build()
        }

        private fun createMediaMetadataCompat(
                mediaId: String,
                title: String,
                artist: String,
                album: String,
                genre: String,
                duration: Long,
                durationUnit: TimeUnit,
                musicFilename: String,
                albumArtResId: Int,
                albumArtResName: String) {
            music[mediaId] = MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                            TimeUnit.MILLISECONDS.convert(getDuration(filePath), durationUnit))
                    .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                    .putString(
                            MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                            getAlbumArtUri(albumArtResName))
                    .putString(
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                            getAlbumArtUri(albumArtResName))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                    .build()
            albumRes[mediaId] = albumArtResId
            musicFileName[mediaId] = musicFilename
        }
    }
}
