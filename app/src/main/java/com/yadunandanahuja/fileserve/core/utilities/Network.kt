package com.yadunandanahuja.fileserve.core.utilities

import java.net.NetworkInterface

fun getFirstIpV4WlanHost(): String? {
    val list = NetworkInterface.getNetworkInterfaces().toList()
    for (item in list) {
        if (!item.name.contains("wlan")) continue
        val inetAddress = item.inetAddresses.toList()
        for (address in inetAddress) {
            if (address.address.size == 4) {
                return address.hostAddress
            }
        }
    }
    return null
}