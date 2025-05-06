package com.yadunandanahuja.fileserve.data.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.yadunandanahuja.fileserve.data.models.FileInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FileInfoRepository {
    @Query("SELECT * FROM FILE_INFO")
    fun findAll(): Flow<List<FileInfoModel>>

    @Insert
    fun save(file: FileInfoModel)

    @Query("DELETE FROM FILE_INFO WHERE ID = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM FILE_INFO")
    fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'FILE_INFO'")
    fun resetSequences()

    @Transaction
    fun truncate() {
        deleteAll()
        resetSequences()
    }
}