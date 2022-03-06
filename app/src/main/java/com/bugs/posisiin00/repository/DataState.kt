package com.bugs.posisiin00.repository
/*
    DataState adalah sebuah sealed class yang mendefinisikan state dari flow data
    DataState berfungsi untuk meringkas dan mempermudah dalam mengetahui kondisi sebuah data flow
    DataState sangat membantu dalam menampilkan data pada ui
 */
sealed class DataState<T> {
    class Loading<T> : DataState<T>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Failed<T>(val message: String) : DataState<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success<T>(data)
        fun <T> failed(message: String) = Failed<T>(message)
    }
}
