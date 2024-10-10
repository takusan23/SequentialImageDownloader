package io.github.takusan23.sequentialimagedownloader

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object DownloadTool {

    private val okHttpClient = OkHttpClient()

    suspend fun downloadFile(
        url: String,
        parentFolder: File
    ) = withContext(Dispatchers.IO) {
        val request = Request.Builder().apply {
            url(url)
            addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
            get()
        }.build()
        val response = okHttpClient.newCall(request).execute()
        val fileName = response.request.url.pathSegments.last()
        val newFile = parentFolder.resolve(fileName).apply { createNewFile() }
        response.body?.byteStream()?.use { inputStream ->
            newFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        newFile
    }

    suspend fun insertMediaStore(
        context: Context,
        file: File,
        folderName: String
    ) = withContext(Dispatchers.IO) {
        val contentValues = contentValuesOf(
            MediaStore.Images.Media.DISPLAY_NAME to file.name,
            MediaStore.Images.Media.RELATIVE_PATH to "${Environment.DIRECTORY_PICTURES}/${folderName}"
        )
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

}