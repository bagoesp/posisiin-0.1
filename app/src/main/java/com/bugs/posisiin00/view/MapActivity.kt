package com.bugs.posisiin00.view

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bugs.posisiin00.R
import com.bugs.posisiin00.databinding.ActivityMapBinding
import com.bugs.posisiin00.model.Sel

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding

    // map
    // mapview
    private lateinit var mCanvas: Canvas
    private val mPaint = Paint()
    private lateinit var mBitmap: Bitmap
    private val mRect = RectF()

    // sel
    private lateinit var selSel: Array<Array<Sel>>
    private var selSize = 0F

    companion object {
        private const val COLS = 7
        private const val ROWS = 20
        private const val WALL = 3F
        private const val PATH = 10F
        private const val INSET = 3F
    }

    init {
        createMap()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBitmap.post { showMap() }
    }

    private fun showMap(){
        val ruangBitmap = BitmapFactory.decodeResource(resources, R.drawable.ruang_kuliah_off_gray_icon)
        val wallBitmap = BitmapFactory.decodeResource(resources, R.drawable.wall_icon)
        val toiletBitmap = BitmapFactory.decodeResource(resources,
            R.drawable.ruang_toilet_off_gray_icon
        )
        val tanggaBitmap = BitmapFactory.decodeResource(resources, R.drawable.tangga_icon)
        val aulaBitmap = BitmapFactory.decodeResource(resources,
            R.drawable.ruang_aula_off_gray_icon
        )
        val kuliahIcon = BitmapFactory.decodeResource(resources, R.drawable.kuliah_off_icon)
        val aulaIcon = BitmapFactory.decodeResource(resources, R.drawable.aula_off_icon)
        val toiletIcon = BitmapFactory.decodeResource(resources, R.drawable.toilet_off_icon)

        mBitmap = Bitmap.createBitmap(binding.ivBitmap.width, binding.ivBitmap.height, Bitmap.Config.ARGB_8888)
        binding.ivBitmap.setImageBitmap(mBitmap)
        mCanvas = Canvas(mBitmap)
        mCanvas.save()

        selSize = (binding.ivBitmap.width/(COLS + 5)).toFloat()
        val hMargin = (binding.ivBitmap.width - (selSize * COLS)) / 2
        val vMargin = (binding.ivBitmap.height - (selSize * ROWS)) / 2

        mCanvas.translate(vMargin-10, hMargin-10)
        // wall
        mCanvas.drawBitmap(wallBitmap, null, RectF(0F, 0F, 7*selSize + 20, 20*selSize + 20), mPaint)
        mCanvas.restore()
        mCanvas.translate(vMargin, hMargin)

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

        // ruang 306
        mCanvas.drawBitmap(ruangBitmap, null,  RectF(INSET,INSET, (3*selSize)-3, (3*selSize)-3), mPaint)
        // ruang 307
        mCanvas.drawBitmap(ruangBitmap, null,  RectF(INSET,3*selSize + INSET, (3*selSize)-INSET, (6*selSize)-INSET), mPaint)
        // ruang 302
        mCanvas.drawBitmap(ruangBitmap, null,  RectF(4*selSize+INSET,3*selSize + INSET, (7*selSize)-INSET, (6*selSize)-INSET), mPaint)
        // ruang 303
        mCanvas.drawBitmap(ruangBitmap, null,  RectF(4*selSize+INSET,6*selSize + INSET, (7*selSize)-INSET, (9*selSize)-INSET), mPaint)
        // ruang 304
        mCanvas.drawBitmap(ruangBitmap, null,  RectF(4*selSize+INSET,9*selSize + INSET, (7*selSize)-INSET, (12*selSize)-INSET), mPaint)
        // ruang 305
        mCanvas.drawBitmap(ruangBitmap, null,  RectF(4*selSize+INSET,12*selSize + INSET, (7*selSize)-INSET, (15*selSize)-INSET), mPaint)

        // toilet pi
        mCanvas.drawBitmap(toiletBitmap, null,  RectF(4*selSize+INSET, INSET, (7*selSize)-INSET, selSize+(selSize/2)-INSET), mPaint)
        // toilet pa
        mCanvas.drawBitmap(toiletBitmap, null,  RectF(4*selSize+INSET, selSize+(selSize/2)+INSET, (7*selSize)-INSET, 3*selSize-INSET), mPaint)

        // tangga atas
        mCanvas.drawBitmap(tanggaBitmap, null,  RectF(INSET, 8*selSize+INSET, (3*selSize)-INSET, 9*selSize-INSET), mPaint)
        // tangga bawah
        mCanvas.drawBitmap(tanggaBitmap, null,  RectF(INSET, 12*selSize+INSET, (3*selSize)-INSET, 13*selSize-INSET), mPaint)

        // aula
        mCanvas.drawBitmap(aulaBitmap, null,  RectF(INSET, 15*selSize+INSET, (7*selSize)-INSET, 20*selSize-INSET), mPaint)

        // icon
        // kuliah
        mCanvas.drawBitmap(kuliahIcon, null, RectF(selSize+ 8, selSize+ 8, 2*selSize- 8, 2*selSize- 8), mPaint)

        binding.ivBitmap.invalidate()
    }

    private fun createMap(){
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
            val x = COLS -1
            selSel[x][y].right = true
        }
        // bottom wall
        for (x in 0 until COLS){
            val y = ROWS -1
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
        selSel[0][9].top = true
        selSel[1][9].top = true
        selSel[2][9].top = true
        // bottom wall
        selSel[0][11].bottom = true
        selSel[1][11].bottom = true
        selSel[2][11].bottom = true

        //koridor 3
        //right wall
        selSel[3][9].right = true
        selSel[3][11].right = true

        // ruang 304
        // top wall
        selSel[4][9].top = true
        selSel[5][9].top = true
        selSel[6][9].top = true
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