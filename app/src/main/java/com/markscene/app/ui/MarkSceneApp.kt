package com.markscene.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.markscene.app.ai.provider.GeminiAdvancedVisionProvider
import com.markscene.app.ai.provider.MlKitLocalImageTagger
import com.markscene.app.ai.provider.MlKitTextRecognizer
import com.markscene.app.core.database.MarkSceneDatabase
import com.markscene.app.core.model.AdvancedAnalysis
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import com.markscene.app.data.backup.BackupManager
import com.markscene.app.data.record.RoomRecordRepository
import com.markscene.app.data.settings.ApiKeyStore
import com.markscene.app.ui.screen.CreateRecordScreen
import com.markscene.app.ui.screen.HomeScreen
import com.markscene.app.ui.screen.PrivacyNoticeScreen
import com.markscene.app.ui.screen.RecordDetailScreen
import com.markscene.app.ui.screen.RecordListScreen
import com.markscene.app.ui.screen.SettingsScreen
import com.markscene.app.ui.screen.SpaceTimelineScreen
import com.markscene.app.ui.screen.CompareScreen
import androidx.fragment.app.FragmentActivity
import com.markscene.app.data.settings.SecurityStore
import com.markscene.app.ui.security.BiometricAuthenticator
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.UUID

private const val HOME_ROUTE = "home"
private const val CREATE_RECORD_ROUTE = "create_record"
private const val CREATE_RECORD_SOURCE_ARG = "source"
private const val SEARCH_ROUTE = "search"
private const val SETTINGS_ROUTE = "settings"
private const val DETAIL_ROUTE = "detail"
private const val DETAIL_ID_ARG = "recordId"
private const val SPACE_TIMELINE_ROUTE = "space_timeline"
private const val SPACE_NAME_ARG = "spaceName"
private const val COMPARE_ROUTE = "compare"
private const val COMPARE_ID1_ARG = "id1"
private const val COMPARE_ID2_ARG = "id2"
private const val PRIVACY_ROUTE = "privacy_notice"

private const val SOURCE_CAPTURE = "capture"
private const val SOURCE_IMPORT = "import"

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS advanced_analysis (
                id TEXT NOT NULL PRIMARY KEY,
                recordId TEXT NOT NULL,
                provider TEXT NOT NULL,
                sceneSummary TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE photo_records ADD COLUMN ocrText TEXT")
    }
}

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE photo_records ADD COLUMN space TEXT")
    }
}

private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS tag_corrections (
                originalName TEXT NOT NULL PRIMARY KEY,
                correctedName TEXT NOT NULL,
                usageCount INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

@Composable
fun MarkSceneApp(sharedImageUri: android.net.Uri? = null) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val database = remember {
        Room.databaseBuilder(
            context.applicationContext,
            MarkSceneDatabase::class.java,
            "markscene.db"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build()
    }
    val localTagger = remember { MlKitLocalImageTagger(context.applicationContext, database.tagCorrectionDao()) }
    val textRecognizer = remember { MlKitTextRecognizer(context.applicationContext) }
    val geminiProvider = remember { GeminiAdvancedVisionProvider(context.applicationContext) }
    val repository = remember { RoomRecordRepository(database.recordDao(), database.advancedAnalysisDao()) }
    val apiKeyStore = remember { ApiKeyStore(context.applicationContext) }
    val securityStore = remember { SecurityStore(context.applicationContext) }
    val backupManager = remember { BackupManager(context.applicationContext, repository) }
    val authenticator = remember { BiometricAuthenticator(activity) }

    var searchQuery by remember { mutableStateOf("") }
    var hasApiKey by remember { mutableStateOf(apiKeyStore.getGeminiApiKey() != null) }
    var isBiometricLockEnabled by remember { mutableStateOf(securityStore.isBiometricLockEnabled()) }
    var isAppLocked by remember { mutableStateOf(isBiometricLockEnabled) }
    var backupStatusMessage by remember { mutableStateOf<String?>(null) }

    // Biometric Authentication on Start
    LaunchedEffect(Unit) {
        if (isBiometricLockEnabled) {
            authenticator.authenticate(
                onSuccess = { 
                    isAppLocked = false
                    if (sharedImageUri != null) {
                        navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_IMPORT?uri=${android.net.Uri.encode(sharedImageUri.toString())}")
                    }
                },
                onError = { message ->
                    backupStatusMessage = "인증 실패: $message"
                }
            )
        } else if (sharedImageUri != null) {
            navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_IMPORT?uri=${android.net.Uri.encode(sharedImageUri.toString())}")
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let {
            scope.launch {
                val result = backupManager.exportBackup(it)
                backupStatusMessage = if (result.isSuccess) "백업이 성공적으로 완료되었습니다." else "백업 실패: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                val result = backupManager.importBackup(it)
                backupStatusMessage = if (result.isSuccess) "${result.getOrNull()}개의 기록을 복구했습니다." else "복구 실패: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    val visibleRecords by repository.search(searchQuery).collectAsState(initial = emptyList())

    if (isAppLocked) {
        // Simple Lock Screen Overlay
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .androidx.compose.foundation.background(androidx.compose.material3.MaterialTheme.colorScheme.background),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                    contentDescription = null,
                    modifier = androidx.compose.ui.Modifier.androidx.compose.foundation.layout.size(80.dp),
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
                androidx.compose.material3.Text(
                    text = "앱이 잠겨 있습니다",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                androidx.compose.material3.Button(
                    onClick = {
                        authenticator.authenticate(
                            onSuccess = { isAppLocked = false },
                            onError = { backupStatusMessage = it }
                        )
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                ) {
                    androidx.compose.material3.Text("인증하여 해제")
                }
            }

            // Message Display for Authentication Errors
            backupStatusMessage?.let {
                androidx.compose.material3.Snackbar(
                    modifier = androidx.compose.ui.Modifier.androidx.compose.foundation.layout.padding(paddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp)).androidx.compose.ui.Alignment.BottomCenter,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    androidx.compose.material3.Text(it)
                }
                androidx.compose.runtime.LaunchedEffect(it) {
                    kotlinx.coroutines.delay(3000)
                    backupStatusMessage = null
                }
            }
        }
    } else {
        NavHost(navController = navController, startDestination = HOME_ROUTE) {
            composable(HOME_ROUTE) {
                HomeScreen(
                    records = visibleRecords,
                    onCapturePhoto = { navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_CAPTURE") },
                    onImportPhoto = { navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_IMPORT") },
                    onOpenSettings = { navController.navigate(SETTINGS_ROUTE) },
                    onOpenSearch = { navController.navigate(SEARCH_ROUTE) },
                    onOpenSpaceTimeline = { spaceName -> navController.navigate("$SPACE_TIMELINE_ROUTE/$spaceName") }
                )
            }
            composable(
                route = "$CREATE_RECORD_ROUTE/{$CREATE_RECORD_SOURCE_ARG}?uri={$DETAIL_ID_ARG}",
                arguments = listOf(
                    navArgument(CREATE_RECORD_SOURCE_ARG) { type = NavType.StringType },
                    navArgument(DETAIL_ID_ARG) { 
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val source = backStackEntry.arguments?.getString(CREATE_RECORD_SOURCE_ARG).orEmpty()
                val initialUri = backStackEntry.arguments?.getString(DETAIL_ID_ARG)?.let { android.net.Uri.parse(it) }
                CreateRecordScreen(
                    source = source,
                    initialImageUri = initialUri,
                    localImageTagger = localTagger,
                    textRecognizer = textRecognizer,
                    onSave = { record ->
                        scope.launch {
                            repository.saveRecord(record)
                            navController.navigate(SEARCH_ROUTE) {
                                popUpTo(HOME_ROUTE)
                            }
                        }
                    },
                    onLearnTagCorrection = { original, corrected ->
                        scope.launch {
                            val dao = database.tagCorrectionDao()
                            val existing = dao.getCorrection(original)
                            if (existing != null) {
                                dao.upsert(existing.copy(correctedName = corrected, usageCount = existing.usageCount + 1, updatedAt = System.currentTimeMillis()))
                            } else {
                                dao.upsert(com.markscene.app.core.database.TagCorrectionEntity(original, corrected))
                            }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(SEARCH_ROUTE) {
                RecordListScreen(
                    records = visibleRecords,
                    onSearch = { searchQuery = it },
                    onDeleteRecord = { recordId -> scope.launch { repository.deleteRecord(recordId) } },
                    onOpenDetail = { recordId -> navController.navigate("$DETAIL_ROUTE/$recordId") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "$DETAIL_ROUTE/{$DETAIL_ID_ARG}",
                arguments = listOf(navArgument(DETAIL_ID_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val recordId = backStackEntry.arguments?.getString(DETAIL_ID_ARG)
                val record = visibleRecords.firstOrNull { it.id == recordId }
                val latestAnalysis by (recordId?.let { repository.observeLatestAnalysis(it) } ?: flowOf(null)).collectAsState(initial = null)
                
                // Fetch history based on the first tag (item name)
                val firstTagName = record?.tags?.firstOrNull()?.name
                val historyRecords by (firstTagName?.let { repository.observeRecordsByTag(it) } ?: flowOf(emptyList())).collectAsState(initial = emptyList())

                if (record != null) {
                    RecordDetailScreen(
                        record = record,
                        latestAnalysis = latestAnalysis,
                        historyRecords = historyRecords,
                        onRunAdvancedAnalysis = { targetRecord ->
                            val apiKey = apiKeyStore.getGeminiApiKey().orEmpty()
                            if (apiKey.isBlank()) error("API key missing")
                            geminiProvider.analyze(targetRecord, apiKey).getOrThrow()
                        },
                        onApplyAdvancedAnalysis = { result ->
                            scope.launch {
                                val now = System.currentTimeMillis()
                                val advancedTags = result.suggestedTags.map { tagName ->
                                    PhotoTag(
                                        id = UUID.randomUUID().toString(),
                                        recordId = record.id,
                                        name = tagName.lowercase(),
                                        rawName = null,
                                        source = TagSource.AdvancedAi,
                                        confidence = null,
                                        userConfirmed = false,
                                        createdAt = now
                                    )
                                }
                                val mergedTags = (record.tags + advancedTags).distinctBy { it.name }
                                val updatedRecord = record.copy(
                                    updatedAt = now,
                                    analysisStatus = AnalysisStatus.AdvancedComplete,
                                    tags = mergedTags
                                )
                                repository.saveRecord(updatedRecord)
                                repository.saveAdvancedAnalysis(
                                    AdvancedAnalysis(
                                        id = UUID.randomUUID().toString(),
                                        recordId = record.id,
                                        provider = if (apiKeyStore.getGeminiApiKey() != null) "gemini" else "mock",
                                        sceneSummary = result.sceneSummary,
                                        createdAt = now
                                    )
                                )
                            }
                        },
                        onDeleteRecord = { id ->
                            scope.launch {
                                repository.deleteRecord(id)
                                navController.popBackStack()
                            }
                        },
                        onOpenOtherRecord = { targetId ->
                            navController.navigate("$DETAIL_ROUTE/$targetId")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(
                route = "$SPACE_TIMELINE_ROUTE/{$SPACE_NAME_ARG}",
                arguments = listOf(navArgument(SPACE_NAME_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val spaceName = backStackEntry.arguments?.getString(SPACE_NAME_ARG).orEmpty()
                val spaceRecords = visibleRecords.filter { it.space == spaceName }
                SpaceTimelineScreen(
                    spaceName = spaceName,
                    records = spaceRecords,
                    onOpenDetail = { id -> navController.navigate("$DETAIL_ROUTE/$id") },
                    onCompare = { id1, id2 -> navController.navigate("$COMPARE_ROUTE/$id1/$id2") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "$COMPARE_ROUTE/{$COMPARE_ID1_ARG}/{$COMPARE_ID2_ARG}",
                arguments = listOf(
                    navArgument(COMPARE_ID1_ARG) { type = NavType.StringType },
                    navArgument(COMPARE_ID2_ARG) { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val id1 = backStackEntry.arguments?.getString(COMPARE_ID1_ARG)
                val id2 = backStackEntry.arguments?.getString(COMPARE_ID2_ARG)
                val record1 = visibleRecords.firstOrNull { it.id == id1 }
                val record2 = visibleRecords.firstOrNull { it.id == id2 }
                if (record1 != null && record2 != null) {
                    CompareScreen(
                        record1 = record1,
                        record2 = record2,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(SETTINGS_ROUTE) {
                val corrections by database.tagCorrectionDao().getAllCorrections().collectAsState(initial = emptyList())
                SettingsScreen(
                    hasApiKey = hasApiKey,
                    isBiometricLockEnabled = isBiometricLockEnabled,
                    tagCorrections = corrections,
                    onToggleBiometricLock = { enabled ->
                        if (enabled && !authenticator.isBiometricAvailable()) {
                            backupStatusMessage = "이 기기는 생체 인식을 지원하지 않습니다."
                        } else {
                            securityStore.setBiometricLockEnabled(enabled)
                            isBiometricLockEnabled = enabled
                        }
                    },
                    onSaveApiKey = { key ->
                        apiKeyStore.saveGeminiApiKey(key)
                        hasApiKey = true
                    },
                    onDeleteApiKey = {
                        apiKeyStore.clearGeminiApiKey()
                        hasApiKey = false
                    },
                    onTestConnection = {
                        if (apiKeyStore.getGeminiApiKey().isNullOrBlank()) {
                            "API Key가 없어 테스트할 수 없습니다."
                        } else {
                            "저장된 API Key를 확인했습니다. 상세 화면에서 고급분석 실행 시 실제 호출을 시도합니다."
                        }
                    },
                    onExportBackup = { exportLauncher.launch("MarkScene_Backup_${System.currentTimeMillis()}.zip") },
                    onImportBackup = { importLauncher.launch(arrayOf("application/zip")) },
                    onDeleteTagCorrection = { original ->
                        scope.launch { database.tagCorrectionDao().delete(original) }
                    },
                    externalMessage = backupStatusMessage,
                    onMessageShown = { backupStatusMessage = null },
                    onOpenPrivacyNotice = { navController.navigate(PRIVACY_ROUTE) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(PRIVACY_ROUTE) {
                PrivacyNoticeScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
