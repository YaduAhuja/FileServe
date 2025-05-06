package com.yadunandanahuja.fileserve

import com.yadunandanahuja.fileserve.data.FileServeRoomDatabase
import com.yadunandanahuja.fileserve.data.repositories.FileInfoRepository

interface AppContainer {
    val fileInfoRepository : FileInfoRepository
}


class FileServeAppContainer(application: FileServeApplication) : AppContainer {
    private val fileServeRoomDatabase = FileServeRoomDatabase.getDatabase(application)
    override val fileInfoRepository = fileServeRoomDatabase.getFileInfoRepository()
}