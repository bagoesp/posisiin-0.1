package com.bugs.posisiin00.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.*
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bugs.posisiin00.R
import com.bugs.posisiin00.algorithm.Astar
import com.bugs.posisiin00.algorithm.Knn
import com.bugs.posisiin00.databinding.ActivityOnlineBinding
import com.bugs.posisiin00.model.DataMasukan
import com.bugs.posisiin00.model.DataSampel
import com.bugs.posisiin00.model.Sel
import com.bugs.posisiin00.repository.DataState
import com.bugs.posisiin00.tools.OnlineWifiReceiver
import com.bugs.posisiin00.tools.WifiConstants
import com.bugs.posisiin00.viewModel.DataSampelViewModel
import com.bugs.posisiin00.viewModel.DataSampelViewModelFactory
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OnlineActivity : BaseActivity() {
    // binding
    private lateinit var binding: ActivityOnlineBinding
    // viewModel
    private lateinit var viewModel: DataSampelViewModel
    // couroutine
    private val uiScope = CoroutineScope(Dispatchers.Main)

    // wi-fi utility
    private lateinit var wifiManager: WifiManager
    private lateinit var wifiReceiver: BroadcastReceiver

    // data masukan & data sampel
    private var dataMasukan: DataMasukan? = null
    private var dataSampelDB: List<DataSampel>? = null
    // var awal: Sel? = null
    private lateinit var awal: String

    // mapview
    private lateinit var mCanvas: Canvas
    private val mPaint = Paint()
    private lateinit var mBitmap: Bitmap
    private val mRect = RectF()

    // sel
    private lateinit var selSel: Array<Array<Sel>>
    private var selSize = 0F

    // sel awal tujuan
    private var selAwal: Sel? = null
    private var selTujuan: Sel? = null

    // array
    lateinit var lokasi: Array<String>
    private lateinit var lokasi_x: IntArray
    private lateinit var lokasi_y: IntArray

    companion object {
        private const val COLS = 7
        private const val ROWS = 20
        private const val WALL = 3F
        private const val PATH = 10F
        private const val INSET = 8F
    }

    init {
        uiScope.launch { getAllDataSampel() }
        createMap()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // View Model
        viewModel = ViewModelProvider(this, DataSampelViewModelFactory())[DataSampelViewModel::class.java]

        lokasi = resources.getStringArray(R.array.lokasi)
        lokasi_x = resources.getIntArray(R.array.lokasi_x)
        lokasi_y = resources.getIntArray(R.array.lokasi_y)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lokasi)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinTujuan.adapter = adapter

        binding.btnNavigation.setOnClickListener {
            val tujuan = binding.spinTujuan.selectedItem.toString()
            initSelTujuan(tujuan)
            if (selAwal!!.x != selTujuan!!.x || selAwal!!.y != selTujuan!!.y){
                showMap()
                showAwal()
                showTujuan()
                refreshSel()
                showRute(selAwal!!, selTujuan!!)
            } else {
                showToast("Lokasi sama!")
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onStart() {
        super.onStart()
        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiReceiver = OnlineWifiReceiver(this, wifiManager)
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiReceiver, intentFilter)
    }
    override fun onStop() {
        unregisterReceiver(wifiReceiver)
        super.onStop()
    }

    // pindai wifi for data masukan
    fun scanSuccess() {
        hideProgressBar()
        showToast("Sukses pindai wi-fi")
        getRSS()
        if (dataMasukan != null && dataSampelDB != null && selTujuan != null) {
            awal = Knn(dataMasukan!!, dataSampelDB!!, 3).cariLokasi()
            val tujuan = binding.spinTujuan.selectedItem.toString()
            initSelTujuan(tujuan)

            showMap()
            showAwal()
            showTujuan()
            showRute(selAwal!!, selTujuan!!)
        }
        else if (dataMasukan != null && dataSampelDB != null) {
            awal = Knn(dataMasukan!!, dataSampelDB!!, 3).cariLokasi()
            showMap()
            showAwal()
        }
    }

    fun scanFailure() {
        hideProgressBar()
        showToast("Gagal pindai wi-fi")
    }
    private fun getRSS() {
        var ap1 = 0
        var ap2 = 0
        var ap3 = 0
        for (wifi in wifiManager.scanResults) {
            when(wifi.SSID) {
                WifiConstants.SSID_AP1 -> {
                    ap1 = wifi.level
                }
                WifiConstants.SSID_AP2 -> {
                    ap2 = wifi.level
                }
                WifiConstants.SSID_AP3 -> {
                    ap3 = wifi.level
                }
            }
        }
        dataMasukan = DataMasukan(ap1, ap2, ap3)
    }
    private fun pindaiWifi() {
        // if wifi on
        if (checkState()) {
            // check permission coarse >= marshmallow
            if (WifiConstants.DEVICE_VERSION >= WifiConstants.M_VERSION)
                checkPermission()
            else
                doScan()
        }
    }
    private fun doScan() {
        showProgressBar("Memindai jaringan wi-fi")
        wifiManager.startScan()
    }
    private fun checkState() : Boolean {
        return when (wifiManager.wifiState) {
            WifiManager.WIFI_STATE_DISABLED -> {
                showToast("Wifi tidak aktif")
                false
            }
            WifiManager.WIFI_STATE_ENABLED -> {
                true
            }
            else -> false
        }
    }
    private fun checkPermission() {
        val selfPermission = ContextCompat.checkSelfPermission(this, WifiConstants.PERMISSION_COARSE)
        val granted = PackageManager.PERMISSION_GRANTED
        if (selfPermission != granted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    WifiConstants.PERMISSION_COARSE,
                    WifiConstants.PERMISSION_FINE,
                    WifiConstants.PERMISSION_NETWORK,
                    WifiConstants.PERMISSION_WIFI
                ),
                WifiConstants.REQUEST_CODE
            )
        } else {
            doScan()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            WifiConstants.REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    doScan()
                else
                    showToast("Permission not granted")
            }
        }
    }

    // download data
    private suspend fun getAllDataSampel() {
        viewModel.getAllDataSampel().collect {
            when(it) {
                is DataState.Loading -> {
                    showProgressBar("Mengunduh data sampel")
                }

                is DataState.Success -> {
                    hideProgressBar()
                    dataSampelDB = it.data
                    pindaiWifi()
                }

                is DataState.Failed -> {
                    hideProgressBar()
                    showToast("Gagal mengunduh data sampel")
                }
            }
        }
    }

    private fun refreshSel() {
        for (x in 0 until COLS) {
            for (y in 0 until ROWS) {
                selSel[x][y].g = 0
                selSel[x][y].h = 0
                selSel[x][y].f = 0
                selSel[x][y].parent = null
            }
        }
    }

    private fun showRute(awal:Sel, tujuan: Sel){
        val rute = Astar(selSel, awal, tujuan).cariRute()
        if (rute.isNotEmpty()) {
            mPaint.color = resources.getColor(R.color.tosca)
            mPaint.strokeWidth = PATH
            for (i in 0 until rute.size) {
                if (i == 0) {
                    //mPaint.color = Color.BLUE
                    val current = rute[i]
                    val next = rute[i+1]
                    mCanvas.save()
                    mCanvas.translate(current.x * selSize, current.y * selSize)
                    drawDir(checkDir(next, current))
                    binding.ivMap.invalidate()
                    mCanvas.restore()
                }
                else if (i>0 && i<rute.size-1) {
                    val previous = rute[i-1]
                    val current = rute[i]
                    val next = rute[i+1]
                    mCanvas.save()
                    mCanvas.translate(current.x * selSize, current.y * selSize)

                    // draw previous and next direction
                    // previous
                    //mPaint.color = Color.CYAN
                    drawDir(checkDir(previous, current))
                    // next
                    //mPaint.color = Color.GREEN
                    drawDir(checkDir(next, current))
                    binding.ivMap.invalidate()
                    mCanvas.restore()
                }
                else {
                    val previous = rute[i-1]
                    val current = rute[i]
                    mCanvas.save()
                    mCanvas.translate(current.x*selSize, current.y*selSize)
                    //mPaint.color = Color.RED
                    drawDir(checkDir(previous, current))
                    binding.ivMap.invalidate()
                    mCanvas.restore()
                }
            }
        }
    }

    private fun drawDir(direction: Direction) {
        when(direction) {
            Direction.B -> {
                Log.d("rute", "barat")
                mCanvas.drawLine(0F, selSize/2, selSize/2+3.5F, selSize/2, mPaint)
            }
            Direction.BL -> {
                Log.d("rute", "barat laut")
                mCanvas.drawLine(0F, 0F, selSize/2+2F, selSize/2+2F, mPaint)
            }
            Direction.U -> {
                Log.d("rute", "utara")
                mCanvas.drawLine(selSize/2, 0F, selSize/2, selSize/2+3.5F, mPaint)
            }
            Direction.TL -> {
                Log.d("rute", "timur laut")
                mCanvas.drawLine(selSize, 0F, selSize/2-2F, selSize/2+2F, mPaint)
            }
            Direction.T -> {
                Log.d("rute", "timur")
                mCanvas.drawLine(selSize, selSize/2, selSize/2-3.5F, selSize/2, mPaint)
            }
            Direction.TG -> {
                Log.d("rute", "tenggara")
                mCanvas.drawLine(selSize, selSize, selSize/2-2F, selSize/2-2F, mPaint)
            }
            Direction.S -> {
                Log.d("rute", "selatan")
                mCanvas.drawLine(selSize/2, selSize/2-3.5F, selSize/2, selSize, mPaint)
            }
            Direction.BD -> {
                Log.d("rute", "barat daya")
                mCanvas.drawLine(0F, selSize, selSize/2+2F, selSize/2-2F, mPaint)
            }
        }
    }

    private fun checkDir(from: Sel, next: Sel) : Direction{
        // barat
        if (from.x < next.x && from.y == next.y)
            return Direction.B
        // barat laut
        if (from.x < next.x && from.y < next.y)
            return Direction.BL
        // utara
        if (from.x == next.x && from.y < next.y)
            return Direction.U
        // timur laut
        if (from.x > next.x && from.y < next.y)
            return Direction.TL
        // timur
        if (from.x > next.x && from.y == next.y)
            return Direction.T
        // tenggara
        if (from.x > next.x && from.y > next.y)
            return Direction.TG
        // selatan
        if (from.x == next.x && from.y > next.y)
            return Direction.S
        // barat daya
        if (from.x < next.x && from.y > next.y)
            return Direction.BD

        return Direction.B
    }

    private fun showAwal() {
        initSelAwal(awal)
        if (selAwal != null) {
            binding.tvPosisiValue.text = awal
            mCanvas.save()
            mCanvas.translate(selAwal!!.x * selSize, selAwal!!.y * selSize)
            mPaint.color = Color.RED
            mRect.set(INSET, INSET, selSize- INSET, selSize- INSET)
            mCanvas.drawRect(mRect, mPaint)
            binding.ivMap.invalidate()
            mCanvas.restore()
        }
    }

    private fun showTujuan() {
        if (selTujuan!=null){
            mCanvas.save()
            mCanvas.translate(selTujuan!!.x*selSize, selTujuan!!.y*selSize)
            mPaint.color = Color.GREEN
            mRect.set(INSET, INSET, selSize- INSET, selSize- INSET)
            mCanvas.drawRect(mRect, mPaint)
            binding.ivMap.invalidate()
            mCanvas.restore()
        }
    }

    private fun initSelAwal(posisi : String) {
        for (i in lokasi.indices){
            when(posisi) {
                lokasi[i] -> {
                    selAwal = selSel[lokasi_x[i]][lokasi_y[i]]
                    break
                }
            }
        }
    }

    private fun initSelTujuan(posisi: String) {
        for (i in lokasi.indices) {
            when(posisi) {
                lokasi[i] -> {
                    selTujuan = selSel[lokasi_x[i]][lokasi_y[i]]
                    break
                }
            }
        }
    }

    private fun showMap(){
        val ruangBitmap = BitmapFactory.decodeResource(resources, R.drawable.ruang_kuliah_off_icon)
        mBitmap = Bitmap.createBitmap(binding.ivMap.width, binding.ivMap.height, Bitmap.Config.ARGB_8888)
        binding.ivMap.setImageBitmap(mBitmap)
        mCanvas = Canvas(mBitmap)
        mCanvas.drawColor(Color.WHITE)
        mPaint.color = Color.GRAY
        mPaint.strokeWidth = WALL

        selSize = (binding.ivMap.width/(COLS+5)).toFloat()
        val hMargin = (binding.ivMap.width - (selSize * COLS)) / 2
        val vMargin = (binding.ivMap.height - (selSize * ROWS)) / 4

        mCanvas.translate(hMargin, vMargin)

        mCanvas.drawBitmap(ruangBitmap, null,  RectF(0F,0F, 3 * selSize, 3*selSize), mPaint)

        /*

        for (x in 0 until COLS) {
            for (y in 0 until ROWS) {
                if (selSel[x][y].top)
                    mCanvas.drawLine(x*selSize, y*selSize, (x+1)*selSize, y*selSize, mPaint)
                if (selSel[x][y].left)
                    mCanvas.drawLine(x*selSize, y*selSize, x*selSize, (y+1)*selSize, mPaint)
                if (selSel[x][y].right)
                    mCanvas.drawLine((x+1)*selSize, y*selSize, (x+1)*selSize, (y+1)*selSize, mPaint)
                if (selSel[x][y].bottom)
                    mCanvas.drawLine(x*selSize, (y+1)*selSize, (x+1)*selSize, (y+1)*selSize, mPaint)
            }
        }

         */

        binding.ivMap.invalidate()
    }

    private fun createMap() {
        selSel = Array(COLS) { Array(ROWS) { Sel() } }
        for (x in 0 until COLS) {
            for (y in 0 until ROWS) {
                selSel[x][y].x = x
                selSel[x][y].y = y
            }
        }

        // edge wall
        // left wall
        for (y in 0 until ROWS) {
            val x = 0
            selSel[x][y].left = true
        }
        // top wall
        for (x in 0 until COLS){
            val y = 0
            selSel[x][y].top = true
        }
        // right wall
        for (y in 0 until ROWS) {
            val x = COLS-1
            selSel[x][y].right = true
        }
        // bottom wall
        for (x in 0 until COLS){
            val y = ROWS-1
            selSel[x][y].bottom = true
        }

        // ruang 306
        // right wall
        selSel[2][0].right = true
        selSel[2][2].right = true
        //bottom wall
        selSel[0][2].bottom = true
        selSel[1][2].bottom = true
        selSel[2][2].bottom = true

        // koridor 1
        //left wall
        selSel[3][0].left = true
        selSel[3][2].left = true
        //right wall
        selSel[3][1].right = true

        // toilet pi
        // bottom wall
        selSel[4][0].bottom = true
        selSel[5][0].bottom = true
        selSel[6][0].bottom = true

        // toilet pa
        // top wall
        selSel[4][1].top = true
        selSel[5][1].top = true
        selSel[6][1].top = true
        //bottom wall
        selSel[4][2].bottom = true
        selSel[5][2].bottom = true
        selSel[6][2].bottom = true

        // ruang 307
        // top wall
        selSel[0][3].top = true
        selSel[1][3].top = true
        selSel[2][3].top = true
        //right wall
        selSel[2][3].right = true
        selSel[2][5].right = true
        //bottom wall
        selSel[0][5].bottom = true
        selSel[1][5].bottom = true
        selSel[2][5].bottom = true

        // koridor 2
        //left wall
        selSel[3][3].left = true
        selSel[3][5].left = true
        //right wall
        selSel[3][3].right = true
        selSel[3][5].right = true

        // ruang 302
        //topwall
        selSel[4][3].top = true
        selSel[5][3].top = true
        selSel[6][3].top = true
        //leftwall
        selSel[4][3].left = true
        selSel[4][5].left = true
        //bottom wall
        selSel[4][5].bottom = true
        selSel[5][5].bottom = true
        selSel[6][5].bottom = true

        // lobi 1
        // top wall
        selSel[0][6].top = true
        selSel[1][6].top = true
        selSel[2][6].top = true
        // bottom wall
        selSel[0][7].bottom = true
        selSel[1][7].bottom = true
        selSel[2][7].bottom = true

        // koridor 2
        // left wall
        selSel[3][8].left = true
        // rigt wall
        selSel[3][6].right = true
        selSel[3][8].right = true

        // ruang 303
        // top wall
        selSel[4][6].top = true
        selSel[5][6].top = true
        selSel[6][6].top = true
        //left wall
        selSel[4][6].left = true
        selSel[4][8].left = true
        //bottom wall
        selSel[4][8].bottom = true
        selSel[5][8].bottom = true
        selSel[6][8].bottom = true

        // lobi 2
        // top wall
        selSel[4][9].top = true
        selSel[5][9].top = true
        selSel[6][9].top = true
        // bottom wall
        selSel[4][11].bottom = true
        selSel[5][11].bottom = true
        selSel[6][11].bottom = true

        //koridor 3
        //right wall
        selSel[3][9].right = true
        selSel[3][11].right = true

        // ruang 304
        // top wall
        selSel[4][9].left = true
        selSel[5][9].left = true
        selSel[6][9].left = true
        // left wall
        selSel[4][9].left = true
        selSel[4][11].left = true
        // bottom wall
        selSel[4][11].bottom = true
        selSel[5][11].bottom = true
        selSel[6][11].bottom = true

        // lobi 3
        //top wall
        selSel[0][13].top = true
        selSel[1][13].top = true
        selSel[2][13].top = true
        // bottom wall
        selSel[0][14].bottom = true
        selSel[1][14].bottom = true
        selSel[2][14].bottom = true

        // koridor 4
        // left wall
        selSel[3][12].left = true
        // right wall
        selSel[3][12].right = true
        selSel[3][14].right = true

        //ruang 305
        // top wall
        selSel[4][12].top = true
        selSel[5][12].top = true
        selSel[6][12].top = true
        // left wall
        selSel[4][12].left = true
        selSel[4][14].left = true
        //bottom wall
        selSel[4][14].bottom = true
        selSel[5][14].bottom = true
        selSel[6][14].bottom = true

        // aula
        // top wall
        selSel[0][15].top = true
        selSel[1][15].top = true
        selSel[2][15].top = true
        selSel[4][15].top = true
        selSel[5][15].top = true
        selSel[6][15].top = true


    }

}

enum class Direction {
    B, BL, U, TL, T, TG, S, BD
}