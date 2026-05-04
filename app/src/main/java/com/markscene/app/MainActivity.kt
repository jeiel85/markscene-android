package com.markscene.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.markscene.app.data.settings.UserPreferences
import com.markscene.app.ui.MarkSceneApp
import com.markscene.app.ui.theme.MarkSceneTheme
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedUri = handleSendImage(intent)
        val userPrefs = UserPreferences(this)

        setContent {
            val useTrueBlack = remember { userPrefs.useTrueBlackDarkMode() }
            val useDynamicColors = remember { userPrefs.useDynamicColors() }

            MarkSceneTheme(
                darkTheme = isSystemInDarkTheme(),
                useTrueBlack = useTrueBlack,
                useDynamicColors = useDynamicColors
            ) {
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
