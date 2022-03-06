package com.bugs.posisiin00.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugs.posisiin00.repository.DataSampelRepository

class DataSampelViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  modelClass.getConstructor(DataSampelRepository::class.java)
            .newInstance(DataSampelRepository())
    }
}