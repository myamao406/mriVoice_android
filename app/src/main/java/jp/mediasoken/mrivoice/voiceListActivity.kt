package jp.mediasoken.mrivoice

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import java.util.*

class voiceListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_list)

        //RecyclerViewを取得。
        val voiceListView = findViewById<RecyclerView>(R.id.voiceListView)
        //LinearLayoutManagerオブジェクトを生成。
        val layout = LinearLayoutManager(this@voiceListActivity)
        //RecyclerViewにレイアウトマネージャーとしてLinearLayoutManagerを設定。
        voiceListView.layoutManager = layout
        //読み上げメニューリストデータを生成。
        val voiceList = createVoiceList()
        //アダプタオブジェクトを生成。
        val adapter = RecyclerListAdapter(voiceList)
        //RecyclerViewにアダプタオブジェクトを設定。
        voiceListView.adapter = adapter

        //区切り専用のオブジェクトを生成。
        val decorator = DividerItemDecoration(this@voiceListActivity, layout.orientation)
        //RecyclerViewに区切り線オブジェクトを設定。
        voiceListView.addItemDecoration(decorator)
    }

    private class Constants {
        companion object {
            val ageLabel = arrayOf("だれでも","子供","大人","老人")
            val genderLabel = arrayOf("どちらでも","男性","女性")
        }
    }
    /**
     * リストビューに表示させる読み上げリストデータを生成するメソッド。
     *
     * @return 生成された読み上げリストデータ。
     */
    private fun createVoiceList():List<Map<String,Any>> {
        val voiceList = ArrayList<Map<String, Any>>()
        var voice:MutableMap<String,Any> = HashMap()

        voice["vID"] = 1
        voice["title"] = "坊っちゃん"
        voice["Age"] = 1
        voice["Gender"] = 0
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] =  1
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] =  "親譲りの無鉄砲で小供の時から損ばかりしている。小学校に居る時分学校の二階から飛び降りて一週間ほど腰を抜かした事がある。なぜそんな無闇をしたと聞く人があるかも知れぬ。別段深い理由でもない。新築の二階から首を出していたら、同級生の一人が冗談に、いくら威張っても、そこから飛び降りる事は出来まい。弱虫やーい。と囃したからである。小使に負ぶさって帰って来た時、おやじが大きな眼をして二階ぐらいから飛び降りて腰を抜かす奴があるかと云ったから、この次は抜かさずに飛んで見せますと答えた。（青空文庫より）"
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 2
        voice["title"] = "徒然草"
        voice["Age"] = 1
        voice["Gender"] = 1
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 2
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] =  "つれづれなるまゝに、日暮らし、硯にむかひて、心にうつりゆくよしなし事を、そこはかとなく書きつくれば、あやしうこそものぐるほしけれ。（Wikipediaより）"
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 3
        voice["title"] = "爆発音がした"
        voice["Age"] = 1
        voice["Gender"] = 2
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 0
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] =  "後ろで大きな爆発音がした。俺は驚いて振り返った。"
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 4
        voice["title"] = "英語"
        voice["Age"] = 2
        voice["Gender"] = 0
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 0
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 5
        voice["title"] = "おとな２"
        voice["Age"] = 2
        voice["Gender"] = 1
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 0
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] = "カタカナ語が苦手な方は「組見本」と呼ぶとよいでしょう。主に書籍やウェブページなどのデザインを作成する時によく使われます。カタカナ語が苦手な方は「組見本」と呼ぶとよいでしょう。主に書籍やウェブページなどのデザインを作成する時によく使われます。書体やレイアウトなどを確認するために用います。この組見本は自由に複製したり頒布することができます。"
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 6
        voice["title"] = "おとな３"
        voice["Age"] = 2
        voice["Gender"] = 2
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 0
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] = "これは正式な文章の代わりに入れて使うダミーテキストです。この組見本は自由に複製したり頒布することができます。カタカナ語が苦手な方は「組見本」と呼ぶとよいでしょう。なお、組見本の「組」とは文字組のことです。活字印刷時代の用語だったと思います。書体やレイアウトなどを確認するために用います。ダミーテキストはダミー文書やダミー文章とも呼ばれることがあります。"
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 7
        voice["title"] = "ながい題名６７８９０１２３４５６７８９０"
        voice["Age"] = 0
        voice["Gender"] = 0
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 0
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] = "書体やレイアウトなどを確認するために用います。主に書籍やウェブページなどのデザインを作成する時によく使われます。文章に特に深い意味はありません。主に書籍やウェブページなどのデザインを作成する時によく使われます。書体やレイアウトなどを確認するために用います。この組見本は自由に複製したり頒布することができます。"
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 8
        voice["title"] = "英語大文字"
        voice["Age"] = 3
        voice["Gender"] = 0
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 0
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] = "LOREM IPSUM DOLOR SIT AMET, CONSECTETUR ADIPISICING ELIT, SED DO EIUSMOD TEMPOR INCIDIDUNT UT LABORE ET DOLORE MAGNA ALIQUA. UT ENIM AD MINIM VENIAM, QUIS NOSTRUD EXERCITATION ULLAMCO LABORIS NISI UT ALIQUIP EX EA COMMODO CONSEQUAT. DUIS AUTE IRURE DOLOR IN REPREHENDERIT IN VOLUPTATE VELIT ESSE CILLUM DOLORE EU FUGIAT NULLA PARIATUR. EXCEPTEUR SINT OCCAECAT CUPIDATAT NON PROIDENT, SUNT IN CULPA QUI OFFICIA DESERUNT MOLLIT ANIM ID EST LABORUM."
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 9
        voice["title"] = "老人１"
        voice["Age"] = 3
        voice["Gender"] = 0
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 0
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] = "彼は背後にひそかな足音を聞いた。それはあまり良い意味を示すものではない。誰がこんな夜更けに、しかもこんな街灯のお粗末な港街の狭い小道で彼をつけて来るというのだ。人生の航路を捻じ曲げ、その獲物と共に立ち去ろうとしている、その丁度今。 彼のこの仕事への恐れを和らげるために、数多い仲間の中に同じ考えを抱き、彼を見守り、待っている者がいるというのか。それとも背後の足音の主は、この街に無数にいる法監視役で、強靭な罰をすぐにも彼の手首にガシャンと下すというのか。彼は足音が止まったことに気が着いた。あわてて辺りを見回す。ふと狭い抜け道に目が止まる。 彼は素早く右に身を翻し、建物の間に消え去った。その時彼は、もう少しで道の真中に転がっていたごみバケツに躓き転ぶところだった。 彼は暗闇の中で道を確かめようとじっと見つめた。どうやら自分の通ってきた道以外にこの中庭からの出道はないようだ。 足音はだんだん近づき、彼には角を曲がる黒い人影が見えた。彼の目は夜の闇の中を必死にさまよい、逃げ道を探す。もうすべては終わりなのか。すべての苦労と準備は水の泡だというのか。 突然、彼の横で扉が風に揺らぎ、ほんのわずかにきしむのを聞いた時、彼は背中を壁に押し付け、追跡者に見付けられないことを願った。この扉は望みの綱として投げかけられた、彼のジレンマからの出口なのだろうか。背中を壁にぴったり押し付けたまま、ゆっくりと彼は開いている扉の方へと身を動かして行った。この扉は彼の救いとなるのだろうか。"
        voiceList.add(voice)

        voice = HashMap()
        voice["vID"] = 10
        voice["title"] = "Wordの使い方"
        voice["Age"] = 3
        voice["Gender"] = 2
        voice["Deadline"] = "2018/04/30 18:00"
        voice["Rank"] = 0
        voice["Created"] = "2018/03/22 12:10:40"
        voice["contents"] = "[挿入] タブのギャラリーには、文書全体の体裁に合わせて調整するためのアイテムが含まれています。これらのギャラリーを使用して、表、ヘッダー、フッター、リスト、表紙や、その他の文書パーツを挿入できます。図、グラフ、図表を作成すると、文書の現在の体裁に合わせて調整されます。文書で選択した文字列の書式は、[ホーム] タブのクイック スタイル ギャラリーで体裁を選択することで簡単に変更できます。[ホーム] タブの他のボタンやオプションを使用して、文字列に書式を直接設定することもできます。ほとんどのボタンやオプションで、現在のテーマの体裁を使用するか、直接指定する書式を使用するかを選択できます。文書全体の体裁を変更するには、[ページ レイアウト] タブで新しいテーマを選択します。クイック スタイル ギャラリーに登録されている体裁を変更するには、現在のクイック スタイル セットを変更するコマンドを使用します。テーマ ギャラリーとクイック スタイル ギャラリーにはリセット コマンドが用意されており、文書の体裁を現在のテンプレートの元の体裁にいつでも戻すことができます。"
        voiceList.add(voice)

        return voiceList
    }

    private inner class RecyclerListViewHolder

    (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvTitle: TextView = itemView.findViewById(R.id.titleLabel)
        var _tvAge: TextView = itemView.findViewById(R.id.ageLabel)
        var _tvGender: TextView = itemView.findViewById(R.id.genderLabel)
        var _tvCreated: TextView = itemView.findViewById(R.id.createdLabel)
        var _tvDeadLine: TextView = itemView.findViewById(R.id.deadLineLabel)
    }

    /**
     * RecyclerViewのアダプタクラス。
     */
    private inner class RecyclerListAdapter
    (
    private val _listData: List<Map<String, Any>>) : RecyclerView.Adapter<RecyclerListViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListViewHolder {
            //レイアウトインフレータを取得。
            val inflater = LayoutInflater.from(this@voiceListActivity)
            //row.xmlをインフレートし、1行分の画面部品とする。
            val view = inflater.inflate(R.layout.row, parent, false)
            //インフレートされた1行分の画面部品にリスナを設定。
            view.setOnClickListener(ItemClickListener())
            //ビューホルダオブジェクトを生成。
//            val holder = RecyclerListViewHolder(view)
            //生成したビューホルダをリターン。
            return RecyclerListViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerListViewHolder, position: Int) {
            //リストデータから該当1行分のデータを取得。
            val item = _listData[position]
            val titleName = item["title"] as String
            val ageStr = Constants.ageLabel[item["Age"] as Int]
            val genderStr = Constants.genderLabel[item["Gender"] as Int]
            val created = item["Created"] as String
            val deadLine = item["Deadline"] as String

            holder._tvTitle.text = titleName
            holder._tvAge.text = ageStr
            holder._tvGender.text = genderStr
            holder._tvCreated.text = created
            holder._tvDeadLine.text = deadLine

        }

        override fun getItemCount(): Int {
            //リストデータ中の件数をリターン。
            return _listData.size
        }
    }

    /**
     * リストをタップした時のリスナクラス。
     */
    private inner class ItemClickListener : View.OnClickListener {

        override fun onClick(view: View) {
            //タップされたLinearLayout内にあるメニュー名表示TextViewを取得。
            val tvTitle = view.findViewById<TextView>(R.id.titleLabel)
            //メニュー名表示TextViewから表示されているメニュー名文字列を取得。
            val titleName = tvTitle.text.toString()
            //トーストに表示する文字列を生成。
            val msg = getString(R.string.msg_header) + titleName
            //トースト表示。
            Toast.makeText(
                    this@voiceListActivity,
                    msg,
                    Toast.LENGTH_SHORT
            ).show()

            //インテントオブジェクトを用意。
            val intent = Intent(this@voiceListActivity, VoiceRecPreparationActivity::class.java)
            //アクティビティを起動。
            startActivity(intent)
        }
    }
}
