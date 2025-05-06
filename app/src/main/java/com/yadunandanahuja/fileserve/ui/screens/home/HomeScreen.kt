package com.yadunandanahuja.fileserve.ui.screens.home

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yadunandanahuja.fileserve.data.models.FileInfoModel

@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.provideFactory())
) {
    val contentResolver = LocalContext.current.contentResolver

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                val fileInfoModel = convertUriToFileInfo(contentResolver, uri)
                if (fileInfoModel !== FileInfoModel.EMPTY)
                    homeScreenViewModel.saveFileInfo(fileInfoModel)
            }
        }
    )

    val uiState by homeScreenViewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            filePicker.launch(listOf("*/*").toTypedArray())
        }) {
            Text("Add Files")
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .border(2.dp, Color.Blue)
        ) {
            items(uiState.fileInfos) {
                Text(it.toString())
            }
        }
    }
}


fun convertUriToFileInfo(contentResolver: ContentResolver, uri: Uri): FileInfoModel {
    Log.i(
        "Activity Result",
        "Screen: $uri"
    )
    val cursor = contentResolver.query(
        uri, arrayOf(
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_SIZE, DocumentsContract.Document.COLUMN_LAST_MODIFIED
        ), null, null, null
    )

    cursor?.use {
        it.apply {
            @SuppressLint("Range")
            while (moveToNext()) {
                val fileName =
                    getString(getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                val fileSize = getLong(getColumnIndex(DocumentsContract.Document.COLUMN_SIZE))
                val lastModified =
                    getLong(getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED))
                val fileInfo = FileInfoModel(fileName, fileSize, lastModified, uri.toString())
                Log.i("Activity Result", "File Info : $fileInfo")
                return fileInfo
            }
        }
    }

    return FileInfoModel.EMPTY
}