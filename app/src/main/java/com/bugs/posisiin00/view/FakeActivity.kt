package com.bugs.posisiin00.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.bugs.posisiin00.R
import com.bugs.posisiin00.databinding.ActivityFakeBinding
import com.bugs.posisiin00.model.DataSampel
import com.bugs.posisiin00.repository.DataState
import com.bugs.posisiin00.viewModel.DataSampelViewModel
import com.bugs.posisiin00.viewModel.DataSampelViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FakeActivity : BaseActivity() {

    private lateinit var binding: ActivityFakeBinding
    private lateinit var lokasi: Array<String>

    // view model
    private lateinit var viewModel: DataSampelViewModel

    // coroutine scope
    val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // view model
        viewModel = ViewModelProvider(this, DataSampelViewModelFactory())[DataSampelViewModel::class.java]

        // spinner
        lokasi = resources.getStringArray(R.array.lokasi)
        val ad = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lokasi)
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinLokasi.adapter = ad

        binding.btnSaveFake.setOnClickListener {
            uiScope.launch {
                addFakeData(ambilData())
            }
        }

        binding.btnBackFake.setOnClickListener {
            val intent = Intent(this, KelolaDataActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun ambilData():DataSampel {
        val ap1 = binding.etAp1.text.toString().toInt()
        val ap2 = binding.etAp2.text.toString().toInt()
        val ap3 = binding.etAp3.text.toString().toInt()
        val lokasi = binding.spinLokasi.selectedItem.toString()
        val waktuInput = System.currentTimeMillis()

        return DataSampel(lokasi, ap1, ap2, ap3, waktuInput)
    }

    private fun clearForm() {
        binding.etAp1.text.clear()
        binding.etAp2.text.clear()
        binding.etAp3.text.clear()
    }

    private suspend fun addFakeData(dataSampel:DataSampel) {
        viewModel.addDataSampel(dataSampel).collect {
            when(it) {
                is DataState.Loading -> {
                    showProgressBar("mengupload data")
                }
                is DataState.Success -> {
                    hideProgressBar()
                    showToast("berhasil upload data")
                    clearForm()
                }
                is DataState.Failed -> {
                    hideProgressBar()
                    showToast("gagal upload data")
                }
            }
        }
    }


}