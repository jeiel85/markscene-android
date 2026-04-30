package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    hasApiKey: Boolean,
    onSaveApiKey: (String) -> Unit,
    onDeleteApiKey: () -> Unit,
    onTestConnection: () -> String,
    onOpenPrivacyNotice: () -> Unit,
    onBack: () -> Unit
) {
    var apiKeyInput by rememberSaveable { mutableStateOf("") }
    var resultMessage by rememberSaveable { mutableStateOf("BYOK는 선택 기능입니다. API Key 없이도 앱 기본 기능이 동작합니다.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = if (hasApiKey) "Gemini API Key 상태: 저장됨" else "Gemini API Key 상태: 없음",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Gemini API Key") }
        )

        Button(
            onClick = {
                if (apiKeyInput.isNotBlank()) {
                    onSaveApiKey(apiKeyInput.trim())
                    resultMessage = "API Key를 기기 내부 암호화 저장소에 저장했습니다."
                    apiKeyInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save API Key")
        }

        Button(onClick = {
            resultMessage = onTestConnection()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Test Connection (Mock)")
        }

        Button(onClick = {
            onDeleteApiKey()
            resultMessage = "API Key를 삭제했습니다."
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Delete API Key")
        }

        Button(onClick = onOpenPrivacyNotice, modifier = Modifier.fillMaxWidth()) {
            Text("Open Privacy Notice")
        }

        Text(
            text = "외부 분석 안내: 고급 AI 분석 실행 시 선택한 사진/프롬프트가 AI 제공자에 전송될 수 있습니다.",
            style = MaterialTheme.typography.bodySmall
        )
        Text(text = resultMessage, style = MaterialTheme.typography.bodySmall)

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
