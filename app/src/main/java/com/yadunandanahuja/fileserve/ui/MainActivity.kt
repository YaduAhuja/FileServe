package com.yadunandanahuja.fileserve.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.yadunandanahuja.fileserve.core.logging.Taggable
import com.yadunandanahuja.fileserve.ui.screens.home.HomeScreen
import com.yadunandanahuja.fileserve.ui.theme.FileServeTheme
import java.net.NetworkInterface

class MainActivity : ComponentActivity(), Taggable {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val list = NetworkInterface.getNetworkInterfaces().toList()
        for (item in list) {
            Log.i(TAG, "Network : Virtual ${item.isVirtual} Loopback : ${item.isLoopback} PTP : ${item.isPointToPoint} index : ${item.index} $item")
            val inetAddress = item.inetAddresses.toList()
            for (address in inetAddress) {
                if (address.address.size == 4)
                    Log.i(TAG, "Network : ${address.hostAddress} size : ${address.address.size}")
            }
            Log.i(TAG, "Network : $inetAddress")
        }

        enableEdgeToEdge()
        setContent {
            FileServeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        HomeScreen()
                    }
                }
            }
        }
    }
}