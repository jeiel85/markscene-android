package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markscene.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyNoticeScreen(onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_privacy_policy), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Hero Icon
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "MarkScene은 사용자의 데이터를 소중히 다루며, 투명한 보안 정책을 지향합니다.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )

            HorizontalDivider(modifier = Modifier.alpha(0.1f))

            PrivacyItem(
                title = "1. 로컬 우선 처리",
                description = "기본적인 사진 태깅 및 저장 작업은 사용자의 기기 내에서만 이루어집니다. 외부 서버로 사진이 자동 업로드되지 않습니다."
            )

            PrivacyItem(
                title = "2. 선택적 로컬 고급 AI",
                description = "사용자가 로컬 VLM 모델 파일을 가져온 경우, 고급 AI 분석도 기기 안에서 처리됩니다. 모델 파일은 앱 내부 저장소에 복사되며 사진은 외부 서버로 전송되지 않습니다."
            )

            PrivacyItem(
                title = "3. 선택적 외부 AI 분석 (BYOK)",
                description = "로컬 모델이 없고 사용자가 명시적으로 '고급 AI 분석'을 실행할 때만, 선택한 이미지와 프롬프트가 사용자가 등록한 API Key를 통해 외부 AI 제공자(Google Gemini 등)에게 전송됩니다."
            )

            PrivacyItem(
                title = "4. 안전한 키 관리",
                description = "사용자가 등록한 API Key는 안드로이드 시스템의 암호화 저장소(EncryptedSharedPreferences)에 안전하게 보관되며, 앱 외부로 유출되지 않습니다."
            )

            PrivacyItem(
                title = "5. 최소한의 권한",
                description = "앱은 기능 수행에 필요한 최소한의 권한(카메라, 사진 선택 등)만을 요구하며, 전체 갤러리를 스캔하거나 무단으로 데이터에 접근하지 않습니다."
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PrivacyItem(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}

private fun Modifier.size(size: androidx.compose.ui.unit.Dp): Modifier = this.width(size).height(size)
