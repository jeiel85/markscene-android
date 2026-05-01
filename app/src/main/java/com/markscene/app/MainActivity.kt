package com.markscene.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.markscene.app.ui.MarkSceneApp
import com.markscene.app.ui.theme.MarkSceneTheme
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedUri = handleSendImage(intent)
        
        setContent {
            MarkSceneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MarkSceneApp(sharedImageUri = sharedUri)
                }
            }
        }
    }

    private fun handleSendImage(intent: Intent): Uri? {
        return if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        } else {
            null
        }
    }
}
