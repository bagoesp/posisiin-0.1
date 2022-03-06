package com.bugs.posisiin00.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bugs.posisiin00.databinding.ActivityKelolaDataBinding
import com.bugs.posisiin00.model.DataSampel
import com.bugs.posisiin00.repository.DataState
import com.bugs.posisiin00.viewModel.DataSampelViewModel
import com.bugs.posisiin00.viewModel.DataSampelViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class KelolaDataActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityKelolaDataBinding
    private lateinit var viewModel: DataSampelViewModel
    lateinit var sampelSelected : DataSampel

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, DataSampelViewModelFactory())[DataSampelViewModel::class.java]

        binding.rvSampelList.layoutManager = LinearLayoutManager(this)

        binding.btnTambahSampel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            binding.btnTambahSampel.id -> {
                //val intent = Intent(this@KelolaDataActivity, TambahSampelActivity::class.java)
                val intent = Intent(this@KelolaDataActivity, FakeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    /*
    Coroutine function to get data sampel
     */

    @SuppressLint("NotifyDataSetChanged")
    suspend fun getAllDataSampel() {
        viewModel.getAllDataSampel().collect { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    showProgressBar("Meminta data")
                }

                is DataState.Success -> {
                    hideProgressBar()
                    val adapter = ItemRecyclerAdapter(this, dataState.data)
                    binding.rvSampelList.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

                is DataState.Failed -> {
                    hideProgressBar()
                    showToast(dataState.message)
                }
            }
        }
    }

    /*
    Coroutine function to delete data sampel
     */

    suspend fun deleteDataSampel(dataSampel: DataSampel) {
        viewModel.deleteDataSampel(dataSampel).collect {
            when(it) {
                is DataState.Loading -> {
                    showProgressBar("Menghapus data sampel")
                }

                is DataState.Success -> {
                    hideProgressBar()
                    showToast("Data sampel berhasil dihapus")
                    getAllDataSampel()
                }

                is DataState.Failed -> {
                    hideProgressBar()
                    showToast("Gagal hapus data sampel : ${it.message}")
                }
            }
        }
    }

    fun dialogHapus(dataSampel: DataSampel) {
        sampelSelected = dataSampel
        HapusDialog(this).show(supportFragmentManager, "dialog hapus item")
    }

    fun deleteSelected() {
        uiScope.launch {
            deleteDataSampel(sampelSelected)
        }
    }

    init {
        uiScope.launch {
            getAllDataSampel()
        }
    }


}