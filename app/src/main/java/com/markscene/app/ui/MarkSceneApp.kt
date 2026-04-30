package com.markscene.app.ui

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
import com.markscene.app.core.database.MarkSceneDatabase
import com.markscene.app.core.model.AdvancedAnalysis
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import com.markscene.app.data.record.RoomRecordRepository
import com.markscene.app.data.settings.ApiKeyStore
import com.markscene.app.ui.screen.CreateRecordScreen
import com.markscene.app.ui.screen.HomeScreen
import com.markscene.app.ui.screen.PrivacyNoticeScreen
import com.markscene.app.ui.screen.RecordDetailScreen
import com.markscene.app.ui.screen.RecordListScreen
import com.markscene.app.ui.screen.SettingsScreen
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

@Composable
fun MarkSceneApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val localTagger = remember { MlKitLocalImageTagger(context.applicationContext) }
    val geminiProvider = remember { GeminiAdvancedVisionProvider(context.applicationContext) }
    val database = remember {
        Room.databaseBuilder(
            context.applicationContext,
            MarkSceneDatabase::class.java,
            "markscene.db"
        ).addMigrations(MIGRATION_1_2).build()
    }
    val repository = remember { RoomRecordRepository(database.recordDao(), database.advancedAnalysisDao()) }
    val apiKeyStore = remember { ApiKeyStore(context.applicationContext) }

    var searchQuery by remember { mutableStateOf("") }
    var hasApiKey by remember { mutableStateOf(apiKeyStore.getGeminiApiKey() != null) }

    val visibleRecords by repository.search(searchQuery).collectAsState(initial = emptyList())

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
                onSave = { record ->
                    scope.launch {
                        repository.saveRecord(record)
                        navController.navigate(SEARCH_ROUTE)
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
                                    provider = if (hasApiKey) "gemini" else "mock",
                                    sceneSummary = result.sceneSummary,
                                    createdAt = now
                                )
                            )
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
                onOpenPrivacyNotice = { navController.navigate(PRIVACY_ROUTE) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(PRIVACY_ROUTE) {
            PrivacyNoticeScreen(onBack = { navController.popBackStack() })
        }
    }
}
