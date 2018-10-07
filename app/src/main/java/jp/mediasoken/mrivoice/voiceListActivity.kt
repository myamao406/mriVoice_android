package jp.mediasoken.mrivoice

//import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_voice_list.*
import kotlinx.android.synthetic.main.app_bar_voice_list.*

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.row.view.*
import java.util.*
import android.widget.Toast
import com.trading212.stickyheader.StickyHeader
import com.trading212.stickyheader.StickyHeaderDecoration
import kotlinx.android.synthetic.main.list_sticky.view.*
import kotlinx.android.synthetic.main.nav_header_voice_list.view.*
import org.jetbrains.anko.selector

class VoiceListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    internal val mTAG = VoiceListActivity::class.java.simpleName  //ログ用タグ
    private var mAuth = FirebaseAuth.getInstance()
    // この定数は要件に応じて用意する
    private val requestMultiPermissions = 101

    private var isAllowedRecodeAudio = false
    private var isAllowedExternalWrite = false

    var voiceList:List<Map<String,Any>> = listOf()
    var bvoiceList:List<Map<String,Any>> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_list)
        setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        {
//            override fun onDrawerClosed(drawerView: View) {
//                super.onDrawerClosed(drawerView)
//            }
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                nav_view.signIntextView.text = currentUser.eMail
                nav_view.versionTextView.text = BuildConfig.VERSION_NAME
            }
        }


        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //RecyclerViewを取得。
        val voiceListView = findViewById<RecyclerView>(R.id.voiceListView)
        //LinearLayoutManagerオブジェクトを生成。
        val layout = LinearLayoutManager(this@VoiceListActivity)
        //RecyclerViewにレイアウトマネージャーとしてLinearLayoutManagerを設定。
        voiceListView.layoutManager = layout
        //読み上げメニューリストデータを生成。

        voiceList = ListData().createVoiceList(this@VoiceListActivity)

        //プロジェクト名、リスト番号でソート
        voiceList = voiceList.sortedWith(compareBy({it["projectID"] as String},{ it["vID"] as Int }))
        bvoiceList = voiceList      //原稿データのバックアップ
        //アダプタオブジェクトを生成。
        val adapter = RecyclerListAdapter(voiceList)
        //RecyclerViewにアダプタオブジェクトを設定。
        voiceListView.adapter = adapter

        //Sticky
        voiceListView.addItemDecoration(StickyHeaderDecoration())
        adapter.notifyDataSetChanged()

        //区切り専用のオブジェクトを生成。
        val decorator = DividerItemDecoration(this@VoiceListActivity, layout.orientation)
        //RecyclerViewに区切り線オブジェクトを設定。
        voiceListView.addItemDecoration(decorator)

        requestPermission()

//        scenarioList = ArrayList<MutableMap<String, Any>>()
        scenario = HashMap()
        scenario["projectID"] = "test"
        scenario["vID"] = 10
        scenario["title"] = "title10"
        scenario["Age"] = 1
        scenario["Gender"] = 2
        scenario["Deadline"] = "2018/10/1"
        scenario["Rank"] = 1
        scenario["Created"] = "2018/10/1"
        scenario["contents"] = "test"
        scenario["isSticky"] = false
        scenario["isRecord"] = false
        scenarioList.add(scenario)

        scenario = HashMap()
        scenario["projectID"] = "test"
        scenario["vID"] = 2
        scenario["title"] = "title2"
        scenario["Age"] = 1
        scenario["Gender"] = 2
        scenario["Deadline"] = "2018/10/1"
        scenario["Rank"] = 1
        scenario["Created"] = "2018/10/1"
        scenario["contents"] = "test"
        scenario["isSticky"] = false
        scenario["isRecord"] = false
        scenarioList.add(scenario)
        Log.d(mTAG, scenarioList[0]["isSticky"].toString())

        scenarioList[0]["isSticky"] = true

        var scenarioList2 = scenarioList.sortedWith(compareBy({ it["vID"] as Int }))

        scenarioList2[0]["isRecord"] = true

        for (scenario in scenarioList2){
            Log.d(mTAG, scenario["title"].toString())
        }

    }

    override fun onStart() {
        super.onStart()

        // 未ログイン
        if (mAuth.currentUser == null) {
            //インテントオブジェクトを用意。
            val intent = Intent(this, LoginActivity::class.java)
            //アクティビティを起動。
            startActivity(intent)

        } else {
            // Firebase のUID
            currentUser.uid = mAuth.currentUser!!.uid
            currentUser.eMail = mAuth.currentUser!!.email!!
//            Log.d(TAG,"getProjectInfo")
            // プロジェクト情報取得
            getProjectInfo()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.voice_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_config -> {
                // Handle the camera action
            }
            R.id.nav_sign_out -> {
                if (mAuth.currentUser != null) {
                    mAuth.signOut()

                    // Firebase のUID
                    currentUser.uid = ""
                    currentUser.eMail = ""

                    //インテントオブジェクトを用意。
                    val intent = Intent(this, LoginActivity::class.java)
                    //アクティビティを起動。
                    startActivity(intent)
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun requestPermission() {
        val permissionExtStorage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionRecAudio = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
        val reqPermissions = ArrayList<String>()

        // パーミッションがアプリに付与されているか確認する
        when{
            permissionExtStorage != PackageManager.PERMISSION_GRANTED -> {
                Log.v("MainActivity", "API Level = " + Build.VERSION.SDK_INT + ": パーミッションが付与されていない")
                reqPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                // パーミッションが付与されていない場合、
                // パーミッションを要求する（ユーザに許可を求めるダイアログを表示する）

//                ActivityCompat.requestPermissions(this@VoiceListActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), RequestCodeWriteExternalStorage)
            }
            else -> {
                isAllowedExternalWrite = true
                Log.d("debug","permissionExtStorage:GRANTED")
            }
        }
        // パーミッションがアプリに付与されているか確認する
        when{
            permissionRecAudio != PackageManager.PERMISSION_GRANTED -> {
                reqPermissions.add(android.Manifest.permission.RECORD_AUDIO)
//                ActivityCompat.requestPermissions(this@VoiceListActivity, arrayOf(android.Manifest.permission.RECORD_AUDIO), RequestCodeRecordAudio)
            }
            else -> {
                isAllowedRecodeAudio = true
                Log.d("debug","permissionRecAudio:GRANTED")
            }
        }

        if (reqPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this,
                    reqPermissions.toArray(arrayOfNulls<String>(reqPermissions.size)),
                    requestMultiPermissions)
        }
        else{
            startLocationActivity()
        }

    }

    // Intent でLocation
    private fun startLocationActivity() {
//        val intent = Intent(application, LocationActivity::class.java)
//        startActivity(intent)
//        finish()
    }

    // 結果の受け取り
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == requestMultiPermissions) {
            if (grantResults.isNotEmpty()) {
                for (i in 0 until permissions.size) {
                    if (permissions[i] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isAllowedExternalWrite = true
                        } else {
                            // それでも拒否された時の対応
                            val toast = Toast.makeText(this,
                                    "外部書込の許可がないので書き込みできません", Toast.LENGTH_SHORT)
                            toast.show()
                            requestPermission()
                        }
                    } else if (permissions[i] == android.Manifest.permission.RECORD_AUDIO) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isAllowedRecodeAudio = true
                        } else {
                            // それでも拒否された時の対応
                            val toast = Toast.makeText(this,
                                    "マイクの許可がないので録音できません", Toast.LENGTH_SHORT)
                            toast.show()
                            requestPermission()
                        }
                    }
                }

                startLocationActivity()

            }
        }
    }

    private fun getProjectInfo() {
        Log.d(mTAG,"GetProjectTask1")

        // 実行
        GetProjectInfo().provide(object : GetProjectInfo.Callback {
            override fun success() {
                Log.d(mTAG,"success")

//                GetVoiceList().provide(object : GetVoiceList.Callback{
//                    override fun success() {
//                        Log.d(mTAG,"success2")
//                    }
//                })
            }
        })
        Log.d(mTAG,"GetProjectTask2")
    }


    /**
     * RecyclerViewのアダプタクラス。
     */
    private inner class RecyclerListAdapter(private val _listData: List<Map<String, Any>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val mStickyItemType = ItemType.STICKY.ordinal

        val mTextItemType = ItemType.SIMPLE_TEXT.ordinal

        override fun getItemCount(): Int {
            //リストデータ中の件数をリターン。
            return _listData.size
        }

        override fun getItemViewType(position: Int) =
                if (_listData[position]["isSticky"] as Boolean) {
                    mStickyItemType
                } else {
                    mTextItemType
                }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val isStickyItem = viewType == mStickyItemType

            val layoutInflater = LayoutInflater.from(parent.context)

            return if (isStickyItem) {
                //list_sticky.xmlをインフレートし、1行分の画面部品とする。
                val view = layoutInflater.inflate(R.layout.list_sticky, parent, false)
                //インフレートされた1行分の画面部品にリスナを設定。
                view.setOnClickListener(ItemHeaderClickListener())
                StickyViewHolder(view)

            } else {
                //row.xmlをインフレートし、1行分の画面部品とする。
                val view = layoutInflater.inflate(R.layout.row, parent, false)
                //インフレートされた1行分の画面部品にリスナを設定。
                view.setOnClickListener(ItemClickListener())
                RecyclerListViewHolder(view)

            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = _listData[position]

            if (holder is RecyclerListViewHolder) {
                holder.itemView.isClickable = true

                holder.tvProjectName.text = item["projectID"] as String
                holder.tvId.text = (item["vID"] as Int).toString()
                holder.tvTitle.text = item["title"] as String
                holder.tvAge.text = Constants.ageLabel[item["Age"] as Int]
                holder.tvGender.text = Constants.genderLabel[item["Gender"] as Int]
                holder.tvCreated.text = item["Created"] as String
                holder.tvDeadLine.text = item["Deadline"] as String

                if (item["isRecord"] as Boolean) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(this@VoiceListActivity,R.color.kana_back))
                }

            } else if (holder is StickyViewHolder){
                holder.itemView.tag = item["projectID"] as String
                holder.headerTitle.text = item["projectName"] as String
            }
        }

        open inner class StickyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), StickyHeader {
                    override val stickyId: String
            get() = _listData[adapterPosition]["projectID"] as String
            var headerTitle: TextView = itemView.headerLabel
        }

        open inner class RecyclerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var tvProjectName: TextView = itemView.projectName
            var tvId: TextView = itemView.idLabel
            var tvTitle: TextView = itemView.titleLabel
            var tvAge: TextView = itemView.ageLabel
            var tvGender: TextView = itemView.genderLabel
            var tvCreated: TextView = itemView.createdLabel
            var tvDeadLine: TextView = itemView.deadLineLabel
        }
    }

    /**
     * リストをタップした時のリスナクラス。
     */
    private inner class ItemClickListener : View.OnClickListener {

        override fun onClick(view: View) {

            val id = view.findViewById<TextView>(R.id.idLabel)
            val intId = Integer.parseInt(id.text.toString())
            val pID = view.findViewById<TextView>(R.id.projectName)
//            voiceList = ListData().createVoiceList()
            //リストデータから該当1行分のデータを取得。
            val recNo = voiceList.indexOfFirst { it["vID"] ==  intId && it["projectID"] == pID.text}
//            val item = voiceList[intId-1]
            Log.d("ItemClickListener",id.text.toString() + ":" + recNo.toString())
            val item = voiceList[recNo]
            voiceText.projectID = item["projectID"] as String
            voiceText.vID = intId
            voiceText.title = item["title"] as String
            voiceText.Age = item["Age"] as Int
            voiceText.Gender = item["Gender"] as Int
            voiceText.Deadline = item["Deadline"] as String
            voiceText.Rank =  item["Rank"] as Int
            voiceText.Created = item["Created"] as String
            voiceText.contents =  item["contents"] as String

            //インテントオブジェクトを用意。
            val intent = Intent(this@VoiceListActivity, VoiceRecPreparationActivity::class.java)
//            val intent = Intent(this@VoiceListActivity, VoiceListActivity::class.java)
            intent.putExtra("ROW", Integer.parseInt(id.text.toString()))
            //アクティビティを起動。R.layout.row
            startActivity(intent)
        }
    }

    /**
     * ヘッダーをタップした時のリスナクラス。
     */
    private inner class ItemHeaderClickListener : View.OnClickListener {

        override fun onClick(view: View) {
//            val projectID = view.tag
//            val projectName = view.findViewById<TextView>(R.id.headerLabel)

//            val recNo = voiceList.indexOfFirst { it["vID"] == 0 && it["projectID"] == projectID }
//            var map = voiceList[recNo]
//            Log.d(mTAG,"map:" + map["accordion"])
//            val aaa = mapOf("accordion" to false)
//            map = aaa
////            voiceList[recNo]["accordion"] = !(voiceList[recNo]["accordion"] as Boolean)
//            Log.d(mTAG,"onClick:" + voiceList[recNo])
        }
    }
}

