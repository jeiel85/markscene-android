package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyNoticeScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Privacy Notice", style = MaterialTheme.typography.headlineSmall)
        Text("- 기본 태깅/저장은 기기 내 로컬에서 동작합니다.")
        Text("- 사용자가 고급 분석을 직접 실행할 때만 선택한 이미지가 외부 AI 제공자에 전송될 수 있습니다.")
        Text("- API Key는 기기 내부 암호화 저장소에만 저장됩니다.")
        Text("- 광범위한 갤러리 스캔/자동 업로드는 수행하지 않습니다.")
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
