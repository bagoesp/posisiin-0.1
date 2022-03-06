package com.bugs.posisiin00.tools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.bugs.posisiin00.view.OnlineActivity

class OnlineWifiReceiver(
    private val activity: OnlineActivity,
    private val wifiManager: WifiManager
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == intent.action) {
            val scanResult = wifiManager.scanResults
            activity.scanSuccess()
        } else {
            activity.scanFailure()
        }
    }
}