package com.yadunandanahuja.fileserve

import android.app.Application
import android.util.Log
import com.yadunandanahuja.fileserve.core.logging.Taggable

class FileServeApplication : Application(), Taggable {
    init {
        Log.i(TAG, "Application Initialized")
    }
}