package com.yadunandanahuja.fileserve.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FILE_INFO")
data class FileInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int,

    @ColumnInfo(name = "FILE_NAME")
    val fileName: String,

    @ColumnInfo(name = "FILE_SIZE")
    val fileSize: Long,

    @ColumnInfo(name = "LAST_MODIFIED")
    val lastModified: Long,

    @ColumnInfo(name = "CONTENT_URI")
    val contentUri: String,
) {
    constructor(fileName: String, fileSize: Long, lastModified: Long, contentUri: String) : this(
        0,
        fileName,
        fileSize,
        lastModified,
        contentUri
    )


    companion object {
        val EMPTY = FileInfoModel(-1, "", 0L, 0L, "")
    }
}