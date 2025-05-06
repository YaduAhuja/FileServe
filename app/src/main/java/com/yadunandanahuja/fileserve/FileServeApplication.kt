package com.yadunandanahuja.fileserve

import android.app.Application
import com.yadunandanahuja.fileserve.core.logging.Taggable

class FileServeApplication : Application(), Taggable {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = FileServeAppContainer(this)
    }
}