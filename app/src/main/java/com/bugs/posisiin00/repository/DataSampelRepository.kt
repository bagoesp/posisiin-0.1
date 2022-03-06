package com.bugs.posisiin00.repository

import com.bugs.posisiin00.model.DataSampel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class DataSampelRepository {
    // Instansiasi firestore class
    private val firestore = Firebase.firestore
    // Mendapatkan data sampel collection
    private val db = firestore.collection(Constant.DATA_SAMPEL_COLLECTION)

     /*
     Flow function to get all data sampel
     */
     fun getAllDataSampel() = flow<DataState<List<DataSampel>>> {
        // Emit loading data state
        emit(DataState.loading())

        // request data to firestore
        val snapshot = db
            .orderBy("waktu_input", Query.Direction.DESCENDING) //sorting data descending
            .get()
            .await()

        // konversi snapshot ke data sampel model
        val dataSampelList = snapshot.toObjects(DataSampel::class.java)

        // emit success data state dengan data
        emit(DataState.success(dataSampelList))
    }.catch {
        // jika task gagal, emit failed data state dengan pesan error
        emit(DataState.failed(it.toString()))
    }.flowOn(Dispatchers.IO)

    /*
    Flow function to add data
     */
    fun addDataSampel(dataSampel: DataSampel) = flow<DataState<Boolean>> {
        // emit loading data state
        emit(DataState.loading())

        // add data
        val id = dataSampel.waktu_input.toString()
        val postRef = db
            .document(id)
            .set(dataSampel)
            .isSuccessful

        // emit success state
        emit(DataState.success(postRef))
    }.catch {
        emit(DataState.failed(it.toString()))
    }.flowOn(Dispatchers.IO)

    /*
    Flow function to delete data
     */
    fun deleteDataSampel(dataSampel: DataSampel) = flow<DataState<Boolean>> {
        // emit loading state
        emit(DataState.loading())

        // delete the data
        val id = dataSampel.waktu_input.toString()
        val delSampel = db
            .document(id)
            .delete()
            .isSuccessful

        // emit success state
        emit(DataState.success(delSampel))
    }.catch {
        // emit failed state
        emit(DataState.failed(it.toString()))
    }.flowOn(Dispatchers.IO)

}