package io.github.takusan23.sequentialimagedownloader.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.github.takusan23.sequentialimagedownloader.DownloadTool
import io.github.takusan23.sequentialimagedownloader.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val url = remember { mutableStateOf("") }
    val startCountText = remember { mutableStateOf("0") }
    val endCountText = remember { mutableStateOf("1") }
    val folderName = remember { mutableStateOf("downloads_${System.currentTimeMillis()}") }

    val isDownloading = remember { mutableStateOf(false) }
    val successCount = remember { mutableIntStateOf(0) }
    val errorCount = remember { mutableIntStateOf(0) }

    fun download() {
        val startCount = startCountText.value.toIntOrNull() ?: return
        val endCount = endCountText.value.toIntOrNull() ?: return
        scope.launch(Dispatchers.IO) {
            val range = startCount..endCount

            // 中身を消す
            val parentFolder = context.getExternalFilesDir(null)!!.resolve("temp").apply { mkdir() }
            parentFolder.listFiles()?.forEach { it.delete() }

            isDownloading.value = true
            successCount.intValue = 0
            errorCount.intValue = 0

            range.forEach { index ->
                try {
                    val buildUrl = url.value.format(index)
                    val downloadFile = DownloadTool.downloadFile(buildUrl, parentFolder)
                    DownloadTool.insertMediaStore(context, downloadFile, folderName.value)
                    successCount.intValue++
                } catch (e: Exception) {
                    e.printStackTrace(System.out)
                    errorCount.intValue++
                }
            }

            isDownloading.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) })
        }
    ) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {

            OutlinedTextField(
                value = url.value,
                onValueChange = { url.value = it },
                label = { Text(text = "URL（%d で数値に置換されます）") }
            )

            Row {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = startCountText.value,
                    onValueChange = { startCountText.value = it },
                    label = { Text(text = "連番ダウンロードの開始値") }
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = endCountText.value,
                    onValueChange = { endCountText.value = it },
                    label = { Text(text = "連番ダウンロードの終了値") }
                )
            }

            OutlinedTextField(
                value = folderName.value,
                onValueChange = { folderName.value = it },
                label = { Text(text = "画像フォルダの子フォルダ名") }
            )

            Button(onClick = { download() }) {
                Text(text = "実行")
            }

            if (isDownloading.value) {
                CircularProgressIndicator()
                Text(text = "ダウンロード中です")
            }

            Text(text = "成功数 = ${successCount.intValue} / 失敗数 = ${errorCount.intValue}")
        }
    }
}