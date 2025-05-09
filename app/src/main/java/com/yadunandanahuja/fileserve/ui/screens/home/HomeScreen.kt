package com.yadunandanahuja.fileserve.ui.screens.home

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract.Document
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
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
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Add Files", style = MaterialTheme.typography.displaySmall)

        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .weight(1f)
                .padding(4.dp)


        ) {
            items(uiState.fileInfos) {
                FileCard(it, homeScreenViewModel::deleteFileInfo)
                HorizontalDivider()
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { filePicker.launch(listOf("*/*").toTypedArray()) }) {
                Text("Add files")
            }
            Button(onClick = {
                homeScreenViewModel.startServer {
                    contentResolver.openInputStream(it.contentUri.toUri())
                }
            }) {
                Text("Start Server")
            }
        }

        if (uiState.serverMessage.isNotEmpty())
            Text(uiState.serverMessage)
    }

}


@Composable
@Preview(backgroundColor = 0)
fun FileCard(
    fileInfo: FileInfoModel = FileInfoModel(
        "Default",
        10L,
        System.currentTimeMillis(),
        ""
    ),
    onClick: (FileInfoModel) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = { onClick(fileInfo) })
            .height(IntrinsicSize.Min)
            .padding(4.dp)
    ) {
        Icon(
            Icons.Outlined.MailOutline, "File Icon", modifier = Modifier
                .weight(0.15f)
                .fillMaxHeight()
        )
        Column(modifier = Modifier.weight(0.85f)) {
            Text(fileInfo.fileName, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${fileInfo.fileSize} | ${fileInfo.lastModified}")
        }
    }
}

fun convertUriToFileInfo(contentResolver: ContentResolver, uri: Uri): FileInfoModel {
    val cursor = contentResolver.query(
        uri, arrayOf(
            Document.COLUMN_DISPLAY_NAME,
            Document.COLUMN_SIZE, Document.COLUMN_LAST_MODIFIED
        ), null, null, null
    )

    cursor?.use {
        it.apply {
            @SuppressLint("Range")
            while (moveToNext()) {
                val fileName =
                    getString(getColumnIndex(Document.COLUMN_DISPLAY_NAME))
                val fileSize = getLong(getColumnIndex(Document.COLUMN_SIZE))
                val lastModified =
                    getLong(getColumnIndex(Document.COLUMN_LAST_MODIFIED))
                val fileInfo = FileInfoModel(fileName, fileSize, lastModified, uri.toString())
                return fileInfo
            }
        }
    }

    return FileInfoModel.EMPTY
}