package jp.mediasoken.mrivoice

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.*

class VoiceDatabaseOpenHelper (context: Context): ManagedSQLiteOpenHelper(context,voiceDB.dbName,null,voiceDB.dbVersion)
{
    private val mTAG = VoiceDatabaseOpenHelper::class.java.simpleName  //ログ用タグ
    companion object {
        val tableProjectInfo = "projectInfo"
        val tableVoiceList = "voiceList"
//        val projectID = "projectID"
//        val projectName = "projectName"
//        val vID = "vID"
//        val isSticky = "isSticky"
//        val accordion = "accordion"
//        val disp = "disp"
//        val ext = "ext"
//        val sampleRate = "sampleRate"
//        val channelsPerFrame = "channelsPerFrame"
//        val bitsPerChannel = "bitsPerChannel"
//        val framesPerPacket = "framesPerPacket"

        private  var instance :VoiceDatabaseOpenHelper? = null

        fun getInstance(context: Context):VoiceDatabaseOpenHelper{
            Log.d("getInstance",  ": getInstance" )
            return instance ?: VoiceDatabaseOpenHelper(context.applicationContext)
        }
    }
    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(mTAG,  ": onCreate" )
        db?.run {
            createTable(
                    tableProjectInfo,
                    ifNotExists = true,
                    columns = *arrayOf(
                            "projectID" to TEXT,
                            "projectName" to TEXT,
                            "vID" to INTEGER,
                            "isSticky" to INTEGER,
                            "accordion" to INTEGER,
                            "disp" to INTEGER,
                            "ext" to TEXT,
                            "sampleRate" to REAL,
                            "channelsPerFrame" to INTEGER,
                            "bitsPerChannel" to INTEGER,
                            "framesPerPacket" to INTEGER
                    )
            )
        }
        Log.d(mTAG,  ": onCreate2" )
        db?.run {
            createTable(
                    tableVoiceList,
                    ifNotExists = true,
                    columns = *arrayOf(
                            "projectID" to TEXT,
                            "vID" to INTEGER,
                            "title" to TEXT,
                            "Age" to INTEGER,
                            "Gender" to INTEGER,
                            "Deadline" to TEXT,
                            "Rank" to INTEGER,
                            "Created" to TEXT,
                            "contents" to INTEGER,
                            "isRecord" to INTEGER
                    )
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}

