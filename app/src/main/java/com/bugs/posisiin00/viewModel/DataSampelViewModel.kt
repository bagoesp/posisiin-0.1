package com.bugs.posisiin00.viewModel

import androidx.lifecycle.ViewModel
import com.bugs.posisiin00.model.DataSampel
import com.bugs.posisiin00.repository.DataSampelRepository

class DataSampelViewModel(private val repository: DataSampelRepository) : ViewModel() {

    fun getAllDataSampel() = repository.getAllDataSampel()

    fun addDataSampel(dataSampel: DataSampel) = repository.addDataSampel(dataSampel)

    fun deleteDataSampel(dataSampel: DataSampel) = repository.deleteDataSampel(dataSampel)

}