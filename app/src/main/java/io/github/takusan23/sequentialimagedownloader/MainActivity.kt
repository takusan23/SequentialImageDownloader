package io.github.takusan23.sequentialimagedownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.takusan23.sequentialimagedownloader.ui.MainScreen
import io.github.takusan23.sequentialimagedownloader.ui.theme.SequentialImageDownloaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SequentialImageDownloaderTheme {
                MainScreen()
            }
        }
    }
}
