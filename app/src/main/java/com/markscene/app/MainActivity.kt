package com.markscene.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.markscene.app.ui.MarkSceneApp
import com.markscene.app.ui.theme.MarkSceneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkSceneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MarkSceneApp()
                }
            }
        }
    }
}
