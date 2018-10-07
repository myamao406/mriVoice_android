package jp.mediasoken.mrivoice

//import jp.mediasoken.mrivoice.projectInfoData
import org.jetbrains.anko.db.MapRowParser

class ListDataParser :MapRowParser<projectInfoData> {
    override fun parseRow(columns: Map<String, Any?>): projectInfoData {

        return projectInfoData(
                columns["projectID"] as String,
                columns["projectName"] as String,
                columns["vID"] as Int,
                columns["isSticky"] as Boolean,
                columns["accordion"] as Boolean,
                columns["disp"] as Boolean,
                columns["ext"] as String,
                columns["sampleRate"] as Double,
                columns["channelsPerFrame"] as Int,
                columns["bitsPerChannel"] as Int,
                columns["framesPerPacket"] as Int)
    }
}