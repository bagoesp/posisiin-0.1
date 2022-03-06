package com.bugs.posisiin00.algorithm

import com.bugs.posisiin00.model.DataMasukan
import com.bugs.posisiin00.model.DataSampel
import com.bugs.posisiin00.model.DataSampelKnn
import com.bugs.posisiin00.model.LOKASI
import com.bugs.posisiin00.repository.Constant
import kotlin.math.sqrt

class Knn(
    private val dataMasukan: DataMasukan,
    private val dataSampelDB: List<DataSampel>,
    private val kValue: Int
) {
    fun cariLokasi() : String {
        // hitung jarak euclidean per data sampel based on data masukan
        // each sampel with ed put into mutable list
        // sort based on ed
        // elliminate based on k value
        // voting
        val newSampelList = mutableListOf<DataSampelKnn>()

        for (sampel in dataSampelDB) {
            val eucDist = calEucDist(dataMasukan, sampel)
            newSampelList.add(DataSampelKnn(sampel.lokasi, eucDist))
        }

        // sorting
        newSampelList.sortedBy { it.euclidean }

        val sortedWithK = newSampelList.take(kValue)
        val voted = sortedWithK.maxByOrNull { it.lokasi!! }!!.lokasi

        return voted!!
    }

    // hitung jarak euclidean
    private fun calEucDist(dataMasukan: DataMasukan, dataSampel: DataSampel): Float {
        val ap1 = pangkat(dataMasukan.ap1 - dataSampel.ap1!!).toFloat()
        val ap2 = pangkat(dataMasukan.ap2 - dataSampel.ap2!!).toFloat()
        val ap3 = pangkat(dataMasukan.ap3 - dataSampel.ap3!!).toFloat()
        return sqrt(ap1 + ap2 + ap3)
    }

    // pangkat
    private fun pangkat(nilai: Int) : Int = nilai * nilai

}