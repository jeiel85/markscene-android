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

@Composable
fun MarkSceneApp(sharedImageUri: android.net.Uri? = null) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val localTagger = remember { MlKitLocalImageTagger(context.applicationContext) }
    val textRecognizer = remember { MlKitTextRecognizer(context.applicationContext) }
    val geminiProvider = remember { GeminiAdvancedVisionProvider(context.applicationContext) }
    val database = remember {
        Room.databaseBuilder(
            context.applicationContext,
            MarkSceneDatabase::class.java,
            "markscene.db"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
    }
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
        Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = androidx.compose.ui.Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "앱이 잠겨 있습니다",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = {
                        authenticator.authenticate(
                            onSuccess = { isAppLocked = false },
                            onError = { backupStatusMessage = it }
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                ) {
                    Text("인증하여 해제")
                }
            }

            // Message Display for Authentication Errors
            backupStatusMessage?.let {
                Snackbar(
                    modifier = androidx.compose.ui.Modifier.padding(paddingValues = PaddingValues(16.dp)).align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(it)
                }
                LaunchedEffect(it) {
                    kotlinx.coroutines.delay(3000)
                    backupStatusMessage = null
                }
            }
        }
    } else {
        NavHost(navController = navController, startDestination = HOME_ROUTE) {
        composable(HOME_ROUTE) {
            HomeScreen(
                onCapturePhoto = { navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_CAPTURE") },
                onImportPhoto = { navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_IMPORT") },
                onOpenSettings = { navController.navigate(SETTINGS_ROUTE) },
                onOpenSearch = { navController.navigate(SEARCH_ROUTE) }
            )
        }
        composable(
            route = "$CREATE_RECORD_ROUTE/{$CREATE_RECORD_SOURCE_ARG}",
            arguments = listOf(navArgument(CREATE_RECORD_SOURCE_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString(CREATE_RECORD_SOURCE_ARG).orEmpty()
            CreateRecordScreen(
                source = source,
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
            if (record != null) {
                RecordDetailScreen(
                    record = record,
                    latestAnalysis = latestAnalysis,
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
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                hasApiKey = hasApiKey,
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
