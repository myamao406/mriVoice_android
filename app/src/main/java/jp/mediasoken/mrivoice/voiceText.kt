package jp.mediasoken.mrivoice

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.HashMap

class voiceText {
    companion object {
        var projectID = ""
        var vID = 0
        var title = ""
        var Age = 0
        var Gender = 0
        var Deadline = ""
        var Rank =  0
        var Created = ""
        var contents =  ""
    }
}

class fileURL {
    companion object {
        val fileNName = "sample.wav"
        var filePath = Environment.getExternalStorageDirectory().path + "/" + fileNName
    }
}

class currentUser {
    companion object {
        var uid = ""
        var eMail = ""
        var gender = "m"
        var country = "jp"
    }
}

class Constants {
    companion object {
        var ageLabel = arrayListOf("だれでも","子供","大人","老人","4〜6歳","6〜10歳")
        var ageLabel_e = arrayListOf("だれでも","子供","大人","老人","4〜6歳","6〜10歳")
        var genderLabel = arrayListOf("どちらでも","男性","女性")
        var genderLabel_e = arrayListOf("どちらでも","男性","女性")
    }
}

class ListData {
    /**
     * リストビューに表示させる読み上げリストデータを生成するメソッド。
     *
     * @return 生成された読み上げリストデータ。
     */
    internal val mTAG = ListData::class.java.simpleName  //ログ用タグ

    companion object {
        val projectLists = ArrayList<Map<String, Any>>()
    }

    fun createVoiceList(context: Context):List<MutableMap<String,Any>> {
        val voiceList = ArrayList<MutableMap<String, Any>>()
        var voice: MutableMap<String, Any> = HashMap()

        val assetManager = context.resources.assets

        try
        {
            voice["projectID"] = "LGWMRI-20180508-02-JP-4-6"
            voice["projectName"] = "4歳~6歳"
            voice["vID"] = 0
            voice["isSticky"] = true
            voice["accordion"] = false
            voice["disp"] = true
            voiceList.add(voice)

            // CSVファイルの読み込み
            val inputStream = assetManager.open("LGWMRI-20180508-02-JP-4-6.csv")
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferReader = BufferedReader(inputStreamReader)
            var line:String?

            line = bufferReader.readLine()
            while (line != null)
            {
                //カンマ区切りで１つづつ配列に入れる
                val RowData = line.split((",").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                //CSVの左([0]番目)から順番にセット

                voice = HashMap()
                voice["projectID"] = RowData[0]
                voice["vID"] = RowData[1].toInt()
                voice["title"] = RowData[2]
                voice["Age"] = RowData[3].toInt()
                voice["Gender"] = RowData[4].toInt()
                voice["Deadline"] = RowData[5]
                voice["Rank"] = RowData[6].toInt()
                voice["Created"] = RowData[7]
                voice["contents"] = RowData[8]
                voice["isSticky"] = RowData[9].toBoolean()
                voice["isRecord"] = RowData[10].toBoolean()
                voiceList.add(voice)
//                Log.d(mTAG, voice.toString())

                line = bufferReader.readLine()
            }
            bufferReader.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

        try
        {
            voice = HashMap()
            voice["projectID"] = "LGWMRI-20180508-02-JP-6-10"
            voice["projectName"] = "6歳~10歳"
            voice["vID"] = 0
            voice["isSticky"] = true
            voice["accordion"] = false
            voice["disp"] = true
            voiceList.add(voice)

            // CSVファイルの読み込み
            val inputStream = assetManager.open("LGWMRI-20180508-02-JP-6-10.csv")
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferReader = BufferedReader(inputStreamReader)
            var line:String?

            line = bufferReader.readLine()
            while (line != null)
            {
                //カンマ区切りで１つづつ配列に入れる
                val RowData = line.split((",").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                //CSVの左([0]番目)から順番にセット

                voice = HashMap()
                voice["projectID"] = RowData[0]
                voice["vID"] = RowData[1].toInt()
                voice["title"] = RowData[2]
                voice["Age"] = RowData[3].toInt()
                voice["Gender"] = RowData[4].toInt()
                voice["Deadline"] = RowData[5]
                voice["Rank"] = RowData[6].toInt()
                voice["Created"] = RowData[7]
                voice["contents"] = RowData[8]
                voice["isSticky"] = RowData[9].toBoolean()
                voice["isRecord"] = RowData[10].toBoolean()
                voiceList.add(voice)
//                Log.d(mTAG, voice.toString())

                line = bufferReader.readLine()
            }
            bufferReader.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }


        Log.d(mTAG, voiceList.count().toString())
        return voiceList
    }
}

class asbd {
    /**
     * 録音レベル情報
     *
     */
    companion object {
        var projectNo: String = ""
        var ext: String = "wav"
        var sampleRate: Double = 44100.0
        var channelsPerFrame: Int = 1
        var bitsPerChannel: Int = 16
        var framesPerPacket: Int = 1
    }
}

var asbds:List<asbd> = listOf()

var foldings : List<Map<String,Boolean>> = listOf()

class voiceDB {
    companion object {
        val dbName = "voicedb.db"
        val dbVersion = 1
    }
}

data class projectInfoData(var projectID:String,
                           var projectName:String,
                           var vID:Int,
                           var isSticky:Boolean,
                           var accordion:Boolean,
                           var disp:Boolean,
                           var ext:String,
                           var sampleRate:Double,
                           var channelsPerFrame:Int,
                           var bitsPerChannel:Int,
                           var framesPerPacket:Int)

var scenarioList = ArrayList<MutableMap<String, Any>>()
var scenario: MutableMap<String, Any> = HashMap()