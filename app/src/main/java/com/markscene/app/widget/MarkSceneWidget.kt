package com.markscene.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.markscene.app.MainActivity
import com.markscene.app.R

/**
 * MarkScene Home Widget
 * Shows quick capture button and recent records count
 */
class MarkSceneWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("markscene_widget", Context.MODE_PRIVATE)
        val recordCount = prefs.getInt("record_count", 0)

        provideContent {
            GlanceTheme {
                WidgetContent(
                    recordCount = recordCount
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        recordCount: Int
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(24.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                // Header with count
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = GlanceModifier.size(32.dp)
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = "MarkScene",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                Text(
                    text = "$recordCount 개의 기록",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface
                    )
                )

                Spacer(modifier = GlanceModifier.height(12.dp))

                // Quick capture button
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(GlanceTheme.colors.primary)
                        .cornerRadius(12.dp)
                        .clickable(
                            actionStartActivity<MainActivity>()
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ 기록하기",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

/**
 * Widget Receiver
 */
class MarkSceneWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MarkSceneWidget()
}