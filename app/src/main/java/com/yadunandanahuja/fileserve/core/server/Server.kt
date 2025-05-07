package com.yadunandanahuja.fileserve.core.server

import com.yadunandanahuja.fileserve.data.models.FileInfoModel
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondOutputStream
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.utils.io.core.Closeable
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.unsafe
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Server(
) : Closeable {
    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? =
        null

    fun start(
        fileInfoList: List<FileInfoModel>,
        fileInputStreamProcessor: (FileInfoModel) -> InputStream?,
    ) {
        close()
        server = initialize(fileInfoList, fileInputStreamProcessor)
        server!!.start()
    }

    private fun initialize(
        fileInfoList: List<FileInfoModel>,
        fileInputStreamProcessor: (FileInfoModel) -> InputStream?,
    ): EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> {
        return embeddedServer(CIO, port = 8080) {
            routing {
                get("/") {
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                    call.respondHtml {
                        head {
                            title { +"File Index" }
                            style {
//                            Using unsafe because kotlin css is in pre-alpha
                                unsafe {
                                    +"""
                                table { border-collapse: collapse; width: 100%; }
                                th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
                                th { background-color: #f2f2f2; }
                                """.trimIndent()
                                }
                            }
                        }
                        body {
                            h1 { +"Available Files" }
                            table {
                                thead {
                                    tr {
                                        th { +"File Name" }
                                        th { +"Size (MB)" }
                                        th { +"Last Modified" }
                                    }
                                }
                                tbody {
                                    for (fileInfo in fileInfoList) {
                                        tr {
                                            td {
                                                a(href = "/download/${fileInfo.fileName}") {
                                                    text(fileInfo.fileName)
                                                }
                                            }
                                            td { +"%.2f".format(fileInfo.fileSize / ((1 shl 20) * 1.0)) }
                                            td { +formatter.format(Date(fileInfo.lastModified)) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                get("/download/{filename}") {
                    val fileName = call.parameters["filename"] ?: ""
                    val fileInfo = fileInfoList.find { it.fileName == fileName }

                    if (fileInfo == null)
                        call.respondText("File not found", status = HttpStatusCode.BadRequest)
                    else {
                        val inputStream = fileInputStreamProcessor(fileInfo)
                        if (inputStream == null)
                            call.respondText(
                                "Unable to open file",
                                status = HttpStatusCode.InternalServerError
                            )
                        else
                            inputStream.use {
                                call.response.headers.append(
                                    HttpHeaders.ContentDisposition,
                                    ContentDisposition.Attachment.withParameter(
                                        ContentDisposition.Parameters.FileName,
                                        fileInfo.fileName
                                    ).toString()
                                )
                                call.respondOutputStream(
                                    ContentType.Application.OctetStream,
                                    HttpStatusCode.OK,
                                    fileInfo.fileSize
                                ) {
                                    inputStream.copyTo(this)
                                }
                            }
                    }
                }
            }
        }
    }

    override fun close() {
        server?.stop()
    }
}