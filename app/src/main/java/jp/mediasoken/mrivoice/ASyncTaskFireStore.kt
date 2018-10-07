package jp.mediasoken.mrivoice

import android.os.AsyncTask
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.db.*

class GetProjectInfo {
    interface Callback {
        fun success()
    }

    /**
     * 処理するよ
     */
    fun provide(callback: Callback) {
        GetProjectTask(callback).execute()
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class GetProjectTask(val callback: Callback) : AsyncTask<Void, Void, Boolean>() {
        private val mTAG = GetProjectTask::class.java.simpleName  //ログ用タグ
        // Access a Cloud Firestore instance from your Activity
        val db:FirebaseFirestore = FirebaseFirestore.getInstance()


        override fun doInBackground(vararg params: Void): Boolean? {
            Log.d(mTAG, "doInBackground")
            try {
                // custom object
                db.collection("projects")
                        .whereEqualTo("isDisabled",false)
                        .get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val helper = VoiceDatabaseOpenHelper.getInstance(MriVoice.appContext)

                                helper.use {
                                    this.delete(VoiceDatabaseOpenHelper.tableProjectInfo)
                                }

//                                var projectInfo: MutableMap<String, Any>
                                for (document in task.result) {
                                    Log.d(mTAG, document.id + " => " + document.data)
                //                                    projectInfo = hashMapOf()
                //                                    projectInfo["projectID"] = document.getId()
                //                                    projectInfo["projectName"] = document.data["projectName"] as String
                //                                    projectInfo["vID"] = 0
                //                                    projectInfo["isSticky"] = true
                //                                    ListData.projectLists.add(projectInfo)

                                    var lext = asbd.ext
                                    if (document.data["ext"] != null) {
                                        lext = document.data["ext"] as String
                                    }
                                    var lsampleRate = asbd.sampleRate
                                    if (document.data["sampleRate"] != null) {
                                        lsampleRate = document.data["sampleRate"] as Double
                                    }
                                    var lchannelsPerFrame = asbd.channelsPerFrame
                                    if (document.data["channelsPerFrame"] != null) {
                                        lchannelsPerFrame = document.data["channelsPerFrame"] as Int
                                    }
                                    var lbitsPerChannel = asbd.bitsPerChannel
                                    if (document.data["bitsPerChannel"] != null){
                                        lbitsPerChannel = document.data["bitsPerChannel"] as Int
                                    }
                                    var lframesPerPacket = asbd.framesPerPacket
                                    if (document.data["framesPerPacket"] != null) {
                                        lframesPerPacket = document.data["framesPerPacket"] as Int
                                    }
                                    Log.d(mTAG, "helper => " + helper + VoiceDatabaseOpenHelper.tableProjectInfo)
                                    helper.use {
                                        this.insert(VoiceDatabaseOpenHelper.tableProjectInfo,
                                                "projectID" to document.id,
                                                "projectName" to document.data["projectName"] as String,
                                                "vID" to 0,
                                                "isSticky" to true,
                                                "accordion" to true,
                                                "disp" to false,
                                                "ext" to lext,
                                                "sampleRate" to lsampleRate,
                                                "channelsPerFrame" to lchannelsPerFrame,
                                                "bitsPerChannel" to lbitsPerChannel,
                                                "framesPerPacket" to lframesPerPacket)
                                    }
                                }
                            } else {
                                Log.d(mTAG, "get failed with ", task.exception)
                            }
                        }
                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                Log.d(mTAG, "return false : " + e.localizedMessage)
                return false
            }

            Log.d(mTAG, "return")

            return true
        }

        override fun onPostExecute(success: Boolean?) {

            if (success!!) {
                Log.d(mTAG, "onPostExecute success")
//                GetVoiceList().provide(object : GetVoiceList.Callback {
//                    override fun success() {
//                        Log.d(mTAG,"success2")
//
//                    }
//                })
            } else {
                Log.d(mTAG, "onPostExecute not success")
            }
        }
    }
}

class GetVoiceList {
    interface Callback {
        fun success()
    }

    /**
     * 処理するよ
     */
    fun provide(callback: Callback) {
        GetVoiceTask(callback).execute()
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class GetVoiceTask(val callback: Callback) : AsyncTask<Void, Void, Boolean>() {
        private val mTAG = GetVoiceTask::class.java.simpleName  //ログ用タグ
        // Access a Cloud Firestore instance from your Activity
        val db:FirebaseFirestore = FirebaseFirestore.getInstance()


        override fun doInBackground(vararg params: Void): Boolean? {
            Log.d(mTAG, "doInBackground")

            val helper = VoiceDatabaseOpenHelper.getInstance(MriVoice.appContext)

            val dataLists = helper.readableDatabase.select(VoiceDatabaseOpenHelper.tableProjectInfo).parseList<projectInfoData>(ListDataParser())

            dataLists.forEach{
                Log.d(mTAG, it.toString())
            }


            try {
                // custom object
                db.collection("projects")
                        .whereEqualTo("isDisabled",false)
                        .get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {



//                                var projectInfo: MutableMap<String, Any>
                                for (document in task.result) {
                                    Log.d(mTAG, document.id + " => " + document.data)
                                    //                                    projectInfo = hashMapOf()
                                    //                                    projectInfo["projectID"] = document.getId()
                                    //                                    projectInfo["projectName"] = document.data["projectName"] as String
                                    //                                    projectInfo["vID"] = 0
                                    //                                    projectInfo["isSticky"] = true
                                    //                                    ListData.projectLists.add(projectInfo)

                                    var lext = asbd.ext
                                    if (document.data["ext"] != null) {
                                        lext = document.data["ext"] as String
                                    }
                                    var lsampleRate = asbd.sampleRate
                                    if (document.data["sampleRate"] != null) {
                                        lsampleRate = document.data["sampleRate"] as Double
                                    }
                                    var lchannelsPerFrame = asbd.channelsPerFrame
                                    if (document.data["channelsPerFrame"] != null) {
                                        lchannelsPerFrame = document.data["channelsPerFrame"] as Int
                                    }
                                    var lbitsPerChannel = asbd.bitsPerChannel
                                    if (document.data["bitsPerChannel"] != null){
                                        lbitsPerChannel = document.data["bitsPerChannel"] as Int
                                    }
                                    var lframesPerPacket = asbd.framesPerPacket
                                    if (document.data["framesPerPacket"] != null) {
                                        lframesPerPacket = document.data["framesPerPacket"] as Int
                                    }
//                                    Log.d(mTAG, "helper => " + helper + VoiceDatabaseOpenHelper.tableProjectInfo)
//                                    helper.use {
//                                        this.insert(VoiceDatabaseOpenHelper.tableProjectInfo,
//                                                "projectID" to document.id,
//                                                "projectName" to document.data["projectName"] as String,
//                                                "vID" to 0, "isSticky" to true,
//                                                "accordion" to true,
//                                                "disp" to false,
//                                                "ext" to lext,
//                                                "sampleRate" to lsampleRate,
//                                                "channelsPerFrame" to lchannelsPerFrame,
//                                                "bitsPerChannel" to lbitsPerChannel,
//                                                "framesPerPacket" to lframesPerPacket)
//                                    }


                                }
                            } else {
                                Log.d(mTAG, "get failed with ", task.exception)
                            }
                        }

                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                Log.d(mTAG, "return false : " + e.localizedMessage)
                return false
            }

            Log.d(mTAG, "return")
            return true
        }

        override fun onPostExecute(success: Boolean?) {

            if (success!!) {
//            finish()
                Log.d(mTAG, "onPostExecute success")
            } else {
                Log.d(mTAG, "onPostExecute not success")
            }
        }
    }
}

// ユーザー情報を取得
class GetUserInfo {
    interface Callback {
        fun success()
    }

    /**
     * 処理するよ
     */
    fun provide(callback: Callback) {
        GetUserTask(callback).execute()
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class GetUserTask(val callback: Callback) : AsyncTask<Void, Void, Boolean>() {
        private val mTAG = GetUserTask::class.java.simpleName  //ログ用タグ
        // Access a Cloud Firestore instance from your Activity
        val db:FirebaseFirestore = FirebaseFirestore.getInstance()

        override fun doInBackground(vararg params: Void): Boolean? {
            Log.d(mTAG, "doInBackground")
            try {
                // custom object
                db.collection("users").document(currentUser.uid)
                        .get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {

//                                val helper = VoiceDatabaseOpenHelper.getInstance(MriVoice.appContext)

//                                helper.use {
//                                    this.delete(VoiceDatabaseOpenHelper.tableProjectInfo)
//                                }

//                                var projectInfo: MutableMap<String, Any>
                                val rgender:String? = task.result["gender"] as String

                                if (rgender != null) {
                                    currentUser.gender = rgender
                                }

                                val rcountry:String? = task.result["country"] as String

                                if (rcountry != null) {
                                    currentUser.country = rcountry
                                }

                            } else {
                                Log.d(mTAG, "get failed with ", task.exception)
                            }
                        }

                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                Log.d(mTAG, "return false : " + e.localizedMessage)
                return false
            }

            try {
                // custom object
                db.collection("ageLabel")
                        .orderBy("ageID")
                        .get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Constants.ageLabel = arrayListOf<String>()
                                Constants.ageLabel_e = arrayListOf<String>()
                                for (document in task.result) {
                                    Constants.ageLabel.add(document.data["ageLabel"] as String)
                                    Constants.ageLabel_e.add(document.data["ageLabelEn"] as String)
                                }
                            } else {
                                Log.d(mTAG, "get failed with ", task.exception)
                            }
                        }

                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                Log.d(mTAG, "return false : " + e.localizedMessage)
                return false
            }

            try {
                // custom object
                db.collection("genderLabel")
                        .orderBy("genderID")
                        .get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Constants.genderLabel = arrayListOf<String>()
                                Constants.genderLabel_e = arrayListOf<String>()
                                for (document in task.result) {
                                    Constants.genderLabel.add(document.data["genderLabel"] as String)
                                    Constants.genderLabel_e.add(document.data["genderLabelEn"] as String)
                                }
                            } else {
                                Log.d(mTAG, "get failed with ", task.exception)
                            }
                        }

                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                Log.d(mTAG, "return false : " + e.localizedMessage)
                return false
            }

            Log.d(mTAG, "return")
            return true
        }

        override fun onPostExecute(success: Boolean?) {

            if (success!!) {
//            finish()
                Log.d(mTAG, "onPostExecute success")
            } else {
                Log.d(mTAG, "onPostExecute not success")
            }
        }
    }
}
