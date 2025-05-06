package com.yadunandanahuja.fileserve.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yadunandanahuja.fileserve.data.models.FileInfoModel
import com.yadunandanahuja.fileserve.data.repositories.FileInfoRepository

@Database(
    entities = [FileInfoModel::class],
    version = 1,
    exportSchema = false
)
abstract class FileServeRoomDatabase : RoomDatabase() {
    abstract fun getFileInfoRepository() : FileInfoRepository

    companion object {
        @Volatile
        private var INSTANCE: FileServeRoomDatabase? = null

        fun getDatabase(context: Context): FileServeRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(
                        context,
                        FileServeRoomDatabase::class.java,
                        "FileServeDB"
                    ).build()
                return INSTANCE!!
            }
        }
    }
}