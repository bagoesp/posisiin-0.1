package com.bugs.posisiin00.tools

import android.os.Build

object WifiConstants {

    const val SSID_AP1 = "Speedy_Instan@wifi.id"
    const val SSID_AP2 = "My_Speedy@B600"
    const val SSID_AP3 = "flashzone-seamless"

    const val REQUEST_CODE = 132

    const val PERMISSION_COARSE = android.Manifest.permission.ACCESS_COARSE_LOCATION
    const val PERMISSION_FINE = android.Manifest.permission.ACCESS_FINE_LOCATION
    const val PERMISSION_WIFI = android.Manifest.permission.ACCESS_WIFI_STATE
    const val PERMISSION_NETWORK = android.Manifest.permission.ACCESS_NETWORK_STATE

    val DEVICE_VERSION = Build.VERSION.SDK_INT
    const val M_VERSION = Build.VERSION_CODES.M

    val label_items = listOf(
        "Aula",
        "Ruang 302",
        "Ruang 303",
        "Ruang 304",
        "Ruang 305",
        "Ruang 306",
        "Ruang 307",
        "Lobi A",
        "Lobi B",
        "Lobi C",
        "Koridor A",
        "Koridor B",
        "Koridor C"
    )
}