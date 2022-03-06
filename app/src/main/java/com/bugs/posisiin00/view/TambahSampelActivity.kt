package com.bugs.posisiin00.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.format.DateUtils
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bugs.posisiin00.R
import com.bugs.posisiin00.databinding.ActivityTambahSampelBinding
import com.bugs.posisiin00.model.DataSampel
import com.bugs.posisiin00.repository.DataState
import com.bugs.posisiin00.tools.OfflineWifiReceiver
import com.bugs.posisiin00.tools.WifiConstants
import com.bugs.posisiin00.viewModel.DataSampelViewModel
import com.bugs.posisiin00.viewModel.DataSampelViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TambahSampelActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTambahSampelBinding

    // wi-fi utility
    private lateinit var wifiManager: WifiManager
    private lateinit var wifiReceiver: BroadcastReceiver

    // view model
    private lateinit var viewModel: DataSampelViewModel

    // coroutine scope
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahSampelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // view model
        viewModel = ViewModelProvider(this, DataSampelViewModelFactory())[DataSampelViewModel::class.java]

        // adapter ac label lokasi
        val labelAdapter = ArrayAdapter(this, R.layout.ac_label_lokasi, WifiConstants.label_items)
        binding.acLabel.setAdapter(labelAdapter)

        binding.btnSimpanData.setOnClickListener(this)
        binding.btnPindaiWifi.setOnClickListener(this)
        binding.btnBackTambah.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            binding.btnSimpanData.id -> {
                if (checkForm())
                    SimpanDialog(this).show(supportFragmentManager, "SimpanDialogFragment")
                else
                    showToast("Data tidak lengkap")
            }

            binding.btnPindaiWifi.id -> {
                pindaiWifi()
            }

            binding.btnBackTambah.id -> {
                val intent = Intent(this, KelolaDataActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, KelolaDataActivity::class.java)
        startActivity(intent)
        finish()
    }

    // onStart to initialize wifi utility
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()

        // wi-fi utility
        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiReceiver = OfflineWifiReceiver(this, wifiManager)

        // wifi receiver intent filter
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiReceiver, intentFilter)
    }

    override fun onStop() {
        unregisterReceiver(wifiReceiver)
        super.onStop()
    }

    // on success scanning
    fun scanSuccess() {
        showToast("Sukses pindai wi-fi")

        // new scan results
        showRSS()
    }

    // on failed scanning
    fun scanFailure() {
        showToast("Gagal pindai wi-fi")
    }

    // onClick pindai button
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

    // do scan wi-fi
    private fun doScan() {
        wifiManager.startScan()
    }

    // check wifi state
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

    // check coarse permission for >= marshmallow
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

    // on request permission coarse
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    // show RSS data
    private fun showRSS() {
        val results = wifiManager.scanResults
        for (wifi in results) {
            when (wifi.SSID) {
                WifiConstants.SSID_AP1 -> {
                    binding.editAp1.setText(wifi.level.toString())
                }
                WifiConstants.SSID_AP2 -> {
                    binding.editAp2.setText(wifi.level.toString())
                }
                WifiConstants.SSID_AP3 -> {
                    binding.editAp3.setText(wifi.level.toString())
                }
            }
        }
    }

    // simpan data
    fun simpanData() {
        uiScope.launch {
            susAddSampel(ambilData())
        }
    }

    // ambil data
    private fun ambilData() : DataSampel {
        val ap1 = binding.editAp1.text.toString().toInt()
        val ap2 = binding.editAp2.text.toString().toInt()
        val ap3 = binding.editAp3.text.toString().toInt()
        val lokasi = binding.acLabel.text.toString()
        val waktu = System.currentTimeMillis()

        return DataSampel(lokasi, ap1, ap2, ap3, waktu)
    }

    // check form
    private fun checkForm() : Boolean{
        return !binding.editAp1.text.isNullOrEmpty() &&
                !binding.editAp2.text.isNullOrEmpty() &&
                !binding.editAp3.text.isNullOrEmpty() &&
                !binding.acLabel.text.isNullOrEmpty()
    }

    // clear form
    private fun clearForm() {
        binding.editAp1.text!!.clear()
        binding.editAp2.text!!.clear()
        binding.editAp3.text!!.clear()
        binding.acLabel.text!!.clear()
    }

    private suspend fun susAddSampel(dataSampel: DataSampel){
        viewModel.addDataSampel(dataSampel).collect {
            when(it) {
                is DataState.Loading -> {
                    showProgressBar("Menyimpan data sampel")
                }

                is DataState.Success -> {
                    hideProgressBar()
                    showToast("Data sampel disimpan")
                    clearForm()
                }

                is DataState.Failed -> {
                    hideProgressBar()
                    showToast("Gagal menyimpan data sampel")
                }
            }
        }
    }

}