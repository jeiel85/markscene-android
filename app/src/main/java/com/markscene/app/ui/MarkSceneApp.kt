package com.markscene.app.ui

import android.net.Uri
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.markscene.app.R
import com.markscene.app.ai.provider.GeminiAdvancedVisionProvider
import com.markscene.app.ai.provider.LocalVisionModelManager
import com.markscene.app.ai.provider.LocalVlmAdvancedVisionProvider
import com.markscene.app.ai.provider.MlKitLocalImageTagger
import com.markscene.app.ai.provider.MlKitTextRecognizer
import com.markscene.app.core.database.MarkSceneDatabase
import com.markscene.app.core.database.TagCorrectionEntity
import com.markscene.app.core.model.AdvancedAnalysis
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.ChatMessage
import com.markscene.app.core.model.MemoryContext
import com.markscene.app.core.model.MemorySource
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import com.markscene.app.data.backup.BackupManager
import com.markscene.app.data.backup.DataExporter
import com.markscene.app.data.record.RoomRecordRepository
import com.markscene.app.data.settings.ApiKeyStore
import com.markscene.app.data.settings.SecurityStore
import com.markscene.app.data.settings.UserPreferences
import com.markscene.app.ui.screen.CompareScreen
import com.markscene.app.ui.screen.CreateRecordScreen
import com.markscene.app.ui.screen.OnboardingScreen
import com.markscene.app.ui.screen.PrivacyDashboardScreen
import com.markscene.app.ui.screen.PrivacyNoticeScreen
import com.markscene.app.ui.screen.RecallScreen
import com.markscene.app.ui.screen.RecordDetailScreen
import com.markscene.app.ui.screen.RecordListScreen
import com.markscene.app.ui.screen.SettingsScreen
import com.markscene.app.ui.screen.SpaceTimelineScreen
import com.markscene.app.ui.screen.TodayScreen
import com.markscene.app.ui.security.BiometricAuthenticator
import com.markscene.app.ui.util.SecureScreenEffect
import com.markscene.app.ui.util.StorageCleaner
import com.markscene.app.ui.util.GalleryHideHelper
import com.markscene.app.ui.util.ReviewHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.UUID

private const val TODAY_ROUTE = "today"
private const val CREATE_RECORD_ROUTE = "create_record"
private const val CREATE_RECORD_SOURCE_ARG = "source"
private const val SEARCH_ROUTE = "search"
private const val RECALL_ROUTE = "recall"
private const val SETTINGS_ROUTE = "settings"
private const val DETAIL_ROUTE = "detail"
private const val DETAIL_ID_ARG = "recordId"
private const val SPACE_TIMELINE_ROUTE = "space_timeline"
private const val SPACE_NAME_ARG = "spaceName"
private const val COMPARE_ROUTE = "compare"
private const val COMPARE_ID1_ARG = "id1"
private const val COMPARE_ID2_ARG = "id2"
private const val PRIVACY_ROUTE = "privacy_notice"
private const val PRIVACY_DASHBOARD_ROUTE = "privacy_dashboard"
private const val ONBOARDING_ROUTE = "onboarding"
private const val SMART_ALBUM_ROUTE = "smart_album"

private const val SOURCE_CAPTURE = "capture"
private const val SOURCE_IMPORT = "import"

private val BOTTOM_NAV_ROUTES = setOf(TODAY_ROUTE, SEARCH_ROUTE, RECALL_ROUTE, SETTINGS_ROUTE)

private val RECALL_KEYWORDS = listOf("나중에", "확인", "만들기", "사야 함", "정리", "TODO")

private fun computeRecallRecords(allRecords: List<PhotoRecord>): List<PhotoRecord> {
    val keywordLower = RECALL_KEYWORDS.map { it.lowercase() }
    return allRecords.filter { record ->
        val memoContains = record.memo?.let { memo ->
            keywordLower.any { memo.lowercase().contains(it) }
        } ?: false
        memoContains
    }.sortedByDescending { it.createdAt }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS advanced_analysis (id TEXT NOT NULL PRIMARY KEY, recordId TEXT NOT NULL, provider TEXT NOT NULL, sceneSummary TEXT NOT NULL, createdAt INTEGER NOT NULL)")
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
        db.execSQL("CREATE TABLE IF NOT EXISTS tag_corrections (originalName TEXT NOT NULL PRIMARY KEY, correctedName TEXT NOT NULL, usageCount INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
    }
}

private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS chat_messages (id TEXT NOT NULL PRIMARY KEY, recordId TEXT NOT NULL, role TEXT NOT NULL, content TEXT NOT NULL, createdAt INTEGER NOT NULL)")
    }
}

private val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS smart_albums (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, coverImageUri TEXT, albumType TEXT NOT NULL, createdAt INTEGER NOT NULL)")
    }
}

private val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE photo_records ADD COLUMN audioMemoUri TEXT")
    }
}

private val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS memory_contexts (id TEXT NOT NULL PRIMARY KEY, recordId TEXT NOT NULL UNIQUE, primaryMemoryType TEXT, mood TEXT, energy INTEGER, contextType TEXT, isWorthRecalling INTEGER NOT NULL DEFAULT 0, recallReason TEXT, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, FOREIGN KEY(recordId) REFERENCES photo_records(id) ON DELETE CASCADE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mc_recordId ON memory_contexts(recordId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mc_primaryMemoryType ON memory_contexts(primaryMemoryType)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mc_isWorthRecalling ON memory_contexts(isWorthRecalling)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mc_createdAt ON memory_contexts(createdAt)")
        db.execSQL("CREATE TABLE IF NOT EXISTS record_memory_types (recordId TEXT NOT NULL, memoryType TEXT NOT NULL, source TEXT NOT NULL, userConfirmed INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL, PRIMARY KEY(recordId, memoryType))")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_rmt_recordId ON record_memory_types(recordId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_rmt_memoryType ON record_memory_types(memoryType)")
    }
}

private val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS records_fts USING fts4(recordId TEXT, title TEXT, memo TEXT, ocrText TEXT, tagsText TEXT, tokenize=unicode61)")
        // 기존 데이터를 FTS 인덱스로 마이그레이션
        db.execSQL("INSERT INTO records_fts(recordId, title, memo, ocrText, tagsText) SELECT r.id, COALESCE(r.title, ''), COALESCE(r.memo, ''), COALESCE(r.ocrText, ''), COALESCE((SELECT GROUP_CONCAT(t.name, ' ') FROM photo_tags t WHERE t.recordId = r.id), '') FROM photo_records r")
    }
}

@Composable
fun MarkSceneApp(sharedImageUri: Uri? = null, appBackgrounded: Boolean = false) {
    val context = LocalContext.current
    val activity = remember(context) {
        var c = context
        while (c is android.content.ContextWrapper) {
            if (c is FragmentActivity) break
            c = c.baseContext
        }
        c as? FragmentActivity
    }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val database = remember {
        try {
            Room.databaseBuilder(context.applicationContext, MarkSceneDatabase::class.java, "markscene.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                .fallbackToDestructiveMigration()
                .build()
        } catch (e: Exception) {
            throw e
        }
    }
    val localTagger = remember { MlKitLocalImageTagger(context.applicationContext, database.tagCorrectionDao()) }
    val textRecognizer = remember { MlKitTextRecognizer(context.applicationContext) }
    val geminiProvider = remember { GeminiAdvancedVisionProvider(context.applicationContext) }
    val apiKeyStore = remember { ApiKeyStore(context.applicationContext) }
    val securityStore = remember { SecurityStore(context.applicationContext) }
    val userPrefs = remember { UserPreferences(context.applicationContext) }
    val localVisionModelManager = remember { LocalVisionModelManager(context.applicationContext, userPrefs) }
    val localVlmProvider = remember { LocalVlmAdvancedVisionProvider(context.applicationContext, localVisionModelManager) }
    val repository = remember { RoomRecordRepository(database.recordDao(), database.advancedAnalysisDao(), database.chatMessageDao(), database.memoryContextDao(), database.recordFtsDao()) }
    val backupManager = remember { BackupManager(context.applicationContext, repository) }
    val authenticator = remember { activity?.let { BiometricAuthenticator(it) } }

    var searchQuery by remember { mutableStateOf("") }
    var showOnboarding by remember { mutableStateOf(!userPrefs.isOnboardingCompleted()) }
    var hasApiKey by remember { mutableStateOf(apiKeyStore.getGeminiApiKey() != null) }
    var localVlmModelName by remember { mutableStateOf(localVisionModelManager.getModelName()) }
    var hasLocalVlmModel by remember { mutableStateOf(localVisionModelManager.getModelPath() != null) }
    var isBiometricLockEnabled by remember {
        mutableStateOf(try { securityStore.isBiometricLockEnabled() } catch (e: Exception) { false })
    }
    var isTrueBlackEnabled by remember { mutableStateOf(userPrefs.useTrueBlackDarkMode()) }
    var isDynamicColorsEnabled by remember { mutableStateOf(userPrefs.useDynamicColors()) }
    var isScreenshotBlockEnabled by remember { mutableStateOf(userPrefs.isScreenshotBlockEnabled()) }
    var isExifStrippingEnabled by remember { mutableStateOf(userPrefs.isExifStrippingEnabled()) }
    var isGalleryHidden by remember { mutableStateOf(userPrefs.isGalleryHidden()) }
    var isAutoLockEnabled by remember { mutableStateOf(userPrefs.isAutoLockEnabled()) }
    var isAppLocked by remember { mutableStateOf(isBiometricLockEnabled) }
    var backupStatusMessage by remember { mutableStateOf<String?>(null) }

    // Apply global FLAG_SECURE when user opts in.
    SecureScreenEffect(enabled = isScreenshotBlockEnabled)

    LaunchedEffect(Unit) {
        // Startup: clean old temp/cache files in background
        StorageCleaner.cleanup(context)
    }

    // Auto-lock: when app returns from background and auto-lock is enabled
    LaunchedEffect(appBackgrounded) {
        if (appBackgrounded && isAutoLockEnabled && isBiometricLockEnabled) {
            isAppLocked = true
        }
    }
    LaunchedEffect(isGalleryHidden) {
        if (isGalleryHidden) {
            GalleryHideHelper.ensureNoMediaForRecordsDir(context)
        }
    }

    LaunchedEffect(Unit) {
        runCatching {
            if (isBiometricLockEnabled && authenticator != null) {
                authenticator.authenticate(
                    onSuccess = {
                        isAppLocked = false
                        if (sharedImageUri != null && !showOnboarding) {
                            navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_IMPORT?uri=${Uri.encode(sharedImageUri.toString())}")
                        }
                    },
                    onError = { message -> backupStatusMessage = "인증 실패: $message" }
                )
            } else if (sharedImageUri != null && !showOnboarding) {
                navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_IMPORT?uri=${Uri.encode(sharedImageUri.toString())}")
            }
        }.onFailure { e ->
            backupStatusMessage = "초기화 중 오류: ${e.message}"
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        uri?.let { scope.launch {
            val result = backupManager.exportBackup(it)
            backupStatusMessage = if (result.isSuccess) "백업이 성공적으로 완료되었습니다." else "백업 실패: ${result.exceptionOrNull()?.message}"
        }}
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { scope.launch {
            val result = backupManager.importBackup(it)
            backupStatusMessage = if (result.isSuccess) "${result.getOrNull()}개의 기록을 복구했습니다." else "복구 실패: ${result.exceptionOrNull()?.message}"
        }}
    }

    val csvExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { scope.launch {
            try {
                val records = repository.observeRecords().first()
                val csvData = DataExporter.toCsv(records)
                context.contentResolver.openOutputStream(it)?.use { out -> out.write(csvData.toByteArray()) }
                backupStatusMessage = "CSV 내보내기가 완료되었습니다."
            } catch (e: Exception) { backupStatusMessage = "CSV 내보내기 실패: ${e.message}" }
        }}
    }

    val mdExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        uri?.let { scope.launch {
            try {
                val records = repository.observeRecords().first()
                val mdData = DataExporter.toMarkdownList(records)
                context.contentResolver.openOutputStream(it)?.use { out -> out.write(mdData.toByteArray()) }
                backupStatusMessage = "Markdown 내보내기가 완료되었습니다."
            } catch (e: Exception) { backupStatusMessage = "Markdown 내보내기 실패: ${e.message}" }
        }}
    }

    val localVlmModelLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            scope.launch {
                val result = localVisionModelManager.importModel(it)
                if (result.isSuccess) {
                    localVlmModelName = result.getOrNull()
                    hasLocalVlmModel = localVisionModelManager.getModelPath() != null
                    backupStatusMessage = context.getString(R.string.settings_local_vlm_imported, localVlmModelName ?: "local model")
                } else {
                    backupStatusMessage = context.getString(
                        R.string.settings_local_vlm_failed,
                        result.exceptionOrNull()?.message ?: "unknown"
                    )
                }
            }
        }
    }

    val allRecordsFlow = remember(repository) {
        repository.observeRecords().catch { emit(emptyList()) }
    }
    val visibleRecordsFlow = remember(repository, searchQuery) {
        repository.search(searchQuery).catch { emit(emptyList()) }
    }
    val allRecords by allRecordsFlow.collectAsState(initial = emptyList())
    val visibleRecords by visibleRecordsFlow.collectAsState(initial = emptyList())

    // In-App Review check (after app stabilizes)
    LaunchedEffect(allRecords.size) {
        if (allRecords.size >= 5 && activity != null) {
            if (ReviewHelper.shouldRequestReview(context, allRecords.size)) {
                ReviewHelper.requestReview(activity, allRecords.size)
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomNav = when {
        currentRoute == null -> true
        currentRoute.startsWith("create_record") -> false
        currentRoute.startsWith("detail") -> false
        currentRoute.startsWith("space_timeline") -> false
        currentRoute.startsWith("compare") -> false
        currentRoute.startsWith("privacy_notice") -> false
        currentRoute.startsWith("privacy_dashboard") -> false
        currentRoute == ONBOARDING_ROUTE -> false
        currentRoute == SMART_ALBUM_ROUTE -> false
        else -> true
    }

    if (showOnboarding) {
        OnboardingScreen(
            onComplete = {
                userPrefs.setOnboardingCompleted(true)
                showOnboarding = false
            }
        )
    } else if (isAppLocked) {
        Box(modifier = Modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(80.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                Text("앱이 잠겨 있습니다", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Button(onClick = {
                    authenticator?.authenticate(onSuccess = { isAppLocked = false }, onError = { backupStatusMessage = it })
                }, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)) {
                    Text("인증하여 해제")
                }
            }
            backupStatusMessage?.let {
                Snackbar(modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter), shape = RoundedCornerShape(12.dp)) { Text(it) }
                LaunchedEffect(it) { kotlinx.coroutines.delay(3000); backupStatusMessage = null }
            }
        }
    } else {
        Scaffold(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_today)) },
                            label = { Text(stringResource(R.string.nav_today)) },
                            selected = currentRoute == TODAY_ROUTE,
                            onClick = {
                                if (currentRoute != TODAY_ROUTE) {
                                    navController.navigate(TODAY_ROUTE) {
                                        popUpTo(TODAY_ROUTE) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.nav_search)) },
                            label = { Text(stringResource(R.string.nav_search)) },
                            selected = currentRoute == SEARCH_ROUTE,
                            onClick = {
                                if (currentRoute != SEARCH_ROUTE) {
                                    navController.navigate(SEARCH_ROUTE) {
                                        popUpTo(TODAY_ROUTE) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = stringResource(R.string.nav_recall)) },
                            label = { Text(stringResource(R.string.nav_recall)) },
                            selected = currentRoute == RECALL_ROUTE,
                            onClick = {
                                if (currentRoute != RECALL_ROUTE) {
                                    navController.navigate(RECALL_ROUTE) {
                                        popUpTo(TODAY_ROUTE) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings)) },
                            label = { Text(stringResource(R.string.nav_settings)) },
                            selected = currentRoute == SETTINGS_ROUTE,
                            onClick = {
                                if (currentRoute != SETTINGS_ROUTE) {
                                    navController.navigate(SETTINGS_ROUTE) {
                                        popUpTo(TODAY_ROUTE) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = TODAY_ROUTE,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(TODAY_ROUTE) {
                    TodayScreen(
                        records = allRecords,
                        onCapturePhoto = { navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_CAPTURE") },
                        onImportPhoto = { navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_IMPORT") },
                        onOpenSearch = { navController.navigate(SEARCH_ROUTE) },
                        onOpenSettings = { navController.navigate(SETTINGS_ROUTE) },
                        onOpenDetail = { recordId -> navController.navigate("$DETAIL_ROUTE/$recordId") }
                    )
                }
                composable(
                    route = "$CREATE_RECORD_ROUTE/{$CREATE_RECORD_SOURCE_ARG}?uri={$DETAIL_ID_ARG}",
                    arguments = listOf(navArgument(CREATE_RECORD_SOURCE_ARG) { type = NavType.StringType }, navArgument(DETAIL_ID_ARG) { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { backStackEntry ->
                    val source = backStackEntry.arguments?.getString(CREATE_RECORD_SOURCE_ARG).orEmpty()
                    val initialUri = backStackEntry.arguments?.getString(DETAIL_ID_ARG)?.let { Uri.parse(it) }
                    CreateRecordScreen(source = source, initialImageUri = initialUri, localImageTagger = localTagger, textRecognizer = textRecognizer, onSave = { record -> scope.launch { repository.saveRecord(record); navController.navigate(SEARCH_ROUTE) { popUpTo(TODAY_ROUTE) } } }, onLearnTagCorrection = { original, corrected -> scope.launch { val dao = database.tagCorrectionDao(); val existing = dao.getCorrection(original); if (existing != null) { dao.upsert(existing.copy(correctedName = corrected, usageCount = existing.usageCount + 1, updatedAt = System.currentTimeMillis())) } else { dao.upsert(TagCorrectionEntity(original, corrected)) } } }, onSaveMemoryTypes = { recordId, memoryTypes, isWorthRecalling -> scope.launch { val now = System.currentTimeMillis(); val primaryType = memoryTypes.firstOrNull(); repository.saveMemoryContext(MemoryContext(id = UUID.randomUUID().toString(), recordId = recordId, primaryMemoryType = primaryType, mood = null, energy = null, contextType = null, isWorthRecalling = isWorthRecalling, recallReason = if (isWorthRecalling) "사용자 지정" else null, createdAt = now, updatedAt = now)); repository.saveMemoryTypes(recordId, memoryTypes, MemorySource.User, true) } }, onBack = { navController.popBackStack() })
                }
                composable(SEARCH_ROUTE) {
                    val allTags = remember(visibleRecords) {
                        visibleRecords.flatMap { it.tags }.map { it.name }.distinct()
                    }
                    val recentSearches = remember { userPrefs.getRecentSearches() }
                    val savedLayout = remember { userPrefs.getPreferredLayout() }
                    RecordListScreen(
                        records = visibleRecords,
                        recentSearches = recentSearches,
                        allTags = allTags,
                        currentLayout = when (savedLayout) {
                            "GRID_3" -> com.markscene.app.ui.screen.LayoutType.GRID_3
                            "LIST" -> com.markscene.app.ui.screen.LayoutType.LIST
                            else -> com.markscene.app.ui.screen.LayoutType.GRID_2
                        },
                        onSearch = { query ->
                            searchQuery = query
                            if (query.isNotBlank()) {
                                userPrefs.addRecentSearch(query)
                            }
                        },
                        onDeleteRecords = { ids -> scope.launch { repository.deleteRecords(ids) } },
                        onMoveToSpace = { ids, space -> scope.launch { repository.updateRecordsSpace(ids, space) } },
                        onOpenDetail = { recordId -> navController.navigate("$DETAIL_ROUTE/$recordId") },
                        onBack = { navController.popBackStack() },
                        onClearRecentSearches = { userPrefs.clearRecentSearches() },
                        onLayoutChange = { layout ->
                            userPrefs.setPreferredLayout(layout.name)
                        }
                    )
                }
                composable(RECALL_ROUTE) {
                    val recallRecords = remember(allRecords) { computeRecallRecords(allRecords) }
                    RecallScreen(
                        recallRecords = recallRecords,
                        onOpenDetail = { recordId -> navController.navigate("$DETAIL_ROUTE/$recordId") }
                    )
                }
                composable(
                    route = "$DETAIL_ROUTE/{$DETAIL_ID_ARG}",
                    arguments = listOf(navArgument(DETAIL_ID_ARG) { type = NavType.StringType })
                ) { backStackEntry ->
                    val recordId = backStackEntry.arguments?.getString(DETAIL_ID_ARG)
                    val record = allRecords.firstOrNull { it.id == recordId }
                    val latestAnalysis by (recordId?.let { repository.observeLatestAnalysis(it) } ?: flowOf(null)).collectAsState(initial = null)
                    val firstTagName = record?.tags?.firstOrNull()?.name
                    val historyRecords by (firstTagName?.let { repository.observeRecordsByTag(it) } ?: flowOf(emptyList())).collectAsState(initial = emptyList())
                    val chatMessages by (recordId?.let { repository.observeMessages(it) } ?: flowOf(emptyList())).collectAsState(initial = emptyList())
                    val memoryContext by (recordId?.let { repository.observeMemoryContext(it) } ?: flowOf(null)).collectAsState(initial = null)
                    val recordMemoryTypes by (recordId?.let { repository.observeMemoryTypes(it) } ?: flowOf(emptyList())).collectAsState(initial = emptyList())

                    if (record != null) {
                        val useLocalVlm = hasLocalVlmModel
                        RecordDetailScreen(
                            record = record,
                            latestAnalysis = latestAnalysis,
                            historyRecords = historyRecords,
                            chatMessages = chatMessages,
                            memoryContext = memoryContext,
                            memoryTypes = recordMemoryTypes,
                            isAdvancedAnalysisAvailable = hasLocalVlmModel || hasApiKey,
                            advancedAnalysisLabel = stringResource(if (useLocalVlm) R.string.detail_run_local_vlm else R.string.detail_run_gemini),
                            advancedAnalysisConsent = stringResource(if (useLocalVlm) R.string.detail_local_vlm_consent_desc else R.string.detail_ai_consent_desc),
                            onRunAdvancedAnalysis = { targetRecord ->
                                if (localVisionModelManager.getModelPath() != null) {
                                    localVlmProvider.analyze(targetRecord).getOrThrow()
                                } else {
                                    val apiKey = apiKeyStore.getGeminiApiKey().orEmpty()
                                    if (apiKey.isBlank()) error("로컬 AI 모델 또는 Gemini API Key가 필요합니다.")
                                    geminiProvider.analyze(targetRecord, apiKey).getOrThrow()
                                }
                            },
                            onApplyAdvancedAnalysis = { result ->
                                scope.launch {
                                    val now = System.currentTimeMillis()
                                    val tagSource = if (localVisionModelManager.getModelPath() != null) TagSource.LocalVlm else TagSource.AdvancedAi
                                    val providerName = if (tagSource == TagSource.LocalVlm) "local_vlm" else "gemini"
                                    val advancedTags = result.suggestedTags.map { tagName ->
                                        PhotoTag(
                                            id = UUID.randomUUID().toString(),
                                            recordId = record.id,
                                            name = tagName.lowercase(),
                                            rawName = null,
                                            source = tagSource,
                                            confidence = null,
                                            userConfirmed = false,
                                            createdAt = now
                                        )
                                    }
                                    val mergedTags = (record.tags + advancedTags).distinctBy { it.name }
                                    val updatedRecord = record.copy(updatedAt = now, analysisStatus = AnalysisStatus.AdvancedComplete, tags = mergedTags)
                                    repository.saveRecord(updatedRecord)
                                    repository.saveAdvancedAnalysis(
                                        AdvancedAnalysis(
                                            id = UUID.randomUUID().toString(),
                                            recordId = record.id,
                                            provider = providerName,
                                            sceneSummary = result.sceneSummary,
                                            createdAt = now
                                        )
                                    )
                                    if (result.memoryTypes.isNotEmpty() || result.recallCandidate) {
                                        val existingContext = repository.getMemoryContext(record.id)
                                        val primaryType = result.memoryTypes.firstOrNull() ?: existingContext?.primaryMemoryType
                                        repository.saveMemoryContext(
                                            MemoryContext(
                                                id = existingContext?.id ?: UUID.randomUUID().toString(),
                                                recordId = record.id,
                                                primaryMemoryType = primaryType,
                                                mood = result.moodSuggestion ?: existingContext?.mood,
                                                energy = existingContext?.energy,
                                                contextType = result.contextType ?: existingContext?.contextType,
                                                isWorthRecalling = result.recallCandidate || (existingContext?.isWorthRecalling == true),
                                                recallReason = result.recallReason ?: existingContext?.recallReason,
                                                createdAt = existingContext?.createdAt ?: now,
                                                updatedAt = now
                                            )
                                        )
                                        if (result.memoryTypes.isNotEmpty()) {
                                            repository.saveMemoryTypes(record.id, result.memoryTypes, MemorySource.AdvancedAi, false)
                                        }
                                    }
                                }
                            },
                            onSendQuestion = { question ->
                                scope.launch {
                                    val apiKey = apiKeyStore.getGeminiApiKey().orEmpty()
                                    if (apiKey.isNotBlank()) {
                                        repository.saveChatMessage(ChatMessage(UUID.randomUUID().toString(), record.id, "user", question, System.currentTimeMillis()))
                                        val response = geminiProvider.askQuestion(record, question, apiKey)
                                        val assistantContent = response.getOrDefault("AI가 사진 분석 중 오류가 발생했습니다.")
                                        repository.saveChatMessage(ChatMessage(UUID.randomUUID().toString(), record.id, "assistant", assistantContent, System.currentTimeMillis()))
                                    } else {
                                        backupStatusMessage = "질문을 위해 Gemini API Key 등록이 필요합니다."
                                    }
                                }
                            },
                            onDeleteRecord = { id -> scope.launch { repository.deleteRecord(id); navController.popBackStack() } },
                            onOpenOtherRecord = { targetId -> navController.navigate("$DETAIL_ROUTE/$targetId") },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(route = "$SPACE_TIMELINE_ROUTE/{$SPACE_NAME_ARG}", arguments = listOf(navArgument(SPACE_NAME_ARG) { type = NavType.StringType })) { backStackEntry ->
                    val spaceName = backStackEntry.arguments?.getString(SPACE_NAME_ARG).orEmpty()
                    val spaceRecords = allRecords.filter { it.space == spaceName }
                    SpaceTimelineScreen(spaceName = spaceName, records = spaceRecords, onOpenDetail = { id -> navController.navigate("$DETAIL_ROUTE/$id") }, onCompare = { id1, id2 -> navController.navigate("$COMPARE_ROUTE/$id1/$id2") }, onBack = { navController.popBackStack() })
                }
                composable(route = "$COMPARE_ROUTE/{$COMPARE_ID1_ARG}/{$COMPARE_ID2_ARG}", arguments = listOf(navArgument(COMPARE_ID1_ARG) { type = NavType.StringType }, navArgument(COMPARE_ID2_ARG) { type = NavType.StringType })) { backStackEntry ->
                    val id1 = backStackEntry.arguments?.getString(COMPARE_ID1_ARG)
                    val id2 = backStackEntry.arguments?.getString(COMPARE_ID2_ARG)
                    val record1 = allRecords.firstOrNull { it.id == id1 }
                    val record2 = allRecords.firstOrNull { it.id == id2 }
                    if (record1 != null && record2 != null) { CompareScreen(record1 = record1, record2 = record2, onBack = { navController.popBackStack() }) }
                }
                composable(SETTINGS_ROUTE) {
                    val corrections by database.tagCorrectionDao().getAllCorrections().collectAsState(initial = emptyList())
                    val weeklyCount = remember(allRecords) {
                        val oneWeekAgo = System.currentTimeMillis() - 7L * 24L * 60L * 60L * 1000L
                        allRecords.count { it.createdAt >= oneWeekAgo }
                    }
                    val weeklyTagCount = remember(allRecords) {
                        allRecords.flatMap { it.tags }.map { it.name }.distinct().size
                    }
                    val achievementBadges = remember(allRecords) {
                        buildList {
                            if (allRecords.size >= 1) add("🏆 첫 기록 작성")
                            if (allRecords.size >= 10) add("📝 정리 습관가 (10+)")
                            if (allRecords.size >= 50) add("👑 기록 마스터 (50+)")
                            val tagged = allRecords.count { it.tags.isNotEmpty() }
                            if (tagged >= 20) add("🏷 태그 장인 (20+)")
                            val uniqueTags = allRecords.flatMap { it.tags }.map { it.name }.distinct().size
                            if (uniqueTags >= 10) add("🔍 탐험가 (10종+ 태그)")
                            if (hasApiKey && allRecords.any { it.analysisStatus.name == "AdvancedComplete" }) add("🤖 AI 탐험가")
                            if (!hasApiKey && allRecords.size >= 20) add("📴 오프라인 마스터")
                        }
                    }
                    val weeklyTopTags = remember(allRecords) {
                        val oneWeekAgo = System.currentTimeMillis() - 7L * 24L * 60L * 60L * 1000L
                        allRecords.filter { it.createdAt >= oneWeekAgo }
                            .flatMap { it.tags }.map { it.name }
                            .groupingBy { it }.eachCount()
                            .entries.sortedByDescending { it.value }.take(3)
                            .joinToString(", ") { "#${it.key}(${it.value})" }
                    }
                    SettingsScreen(
                        hasApiKey = hasApiKey,
                        hasLocalVlmModel = hasLocalVlmModel,
                        localVlmModelName = localVlmModelName,
                        isBiometricLockEnabled = isBiometricLockEnabled,
                        isTrueBlackEnabled = isTrueBlackEnabled,
                        isDynamicColorsEnabled = isDynamicColorsEnabled,
                        isScreenshotBlockEnabled = isScreenshotBlockEnabled,
                        isExifStrippingEnabled = isExifStrippingEnabled,
                        isGalleryHidden = isGalleryHidden,
                        isAutoLockEnabled = isAutoLockEnabled,
                        tagCorrections = corrections,
                        weeklyRecap = "최근 7일: ${weeklyCount}개 기록, ${weeklyTagCount}개 고유 태그" +
                            if (weeklyTopTags.isNotEmpty()) "\n인기 태그: $weeklyTopTags" else "",
                        achievementBadges = achievementBadges,
                        onToggleBiometricLock = { enabled ->
                            if (enabled && (authenticator == null || !authenticator.isBiometricAvailable())) {
                                backupStatusMessage = if (authenticator == null) "시스템 오류로 인증 기능을 사용할 수 없습니다." else "이 기기는 생체 인식을 지원하지 않습니다."
                            } else if (!securityStore.isAvailable()) {
                                backupStatusMessage = "보안 저장소를 사용할 수 없어 생체 잠금을 저장할 수 없습니다."
                            } else {
                                val saved = securityStore.setBiometricLockEnabled(enabled)
                                isBiometricLockEnabled = saved && enabled
                                backupStatusMessage = if (saved) null else "생체 잠금 설정 저장에 실패했습니다."
                            }
                        },
                        onToggleTrueBlack = { enabled ->
                            userPrefs.setTrueBlackDarkMode(enabled)
                            isTrueBlackEnabled = enabled
                            backupStatusMessage = if (enabled) "True Black 모드가 활성화되었습니다. 앱을 재시작하면 적용됩니다." else "True Black 모드가 비활성화되었습니다."
                        },
                        onToggleDynamicColors = { enabled ->
                            userPrefs.setDynamicColors(enabled)
                            isDynamicColorsEnabled = enabled
                            backupStatusMessage = if (enabled) "Material You 동적 색상이 활성화되었습니다. 앱을 재시작하면 적용됩니다." else "Material You 동적 색상이 비활성화되었습니다."
                        },
                        onToggleScreenshotBlock = { enabled ->
                            userPrefs.setScreenshotBlockEnabled(enabled)
                            isScreenshotBlockEnabled = enabled
                            backupStatusMessage = context.getString(
                                if (enabled) R.string.settings_screenshot_block_on
                                else R.string.settings_screenshot_block_off
                            )
                        },
                        onToggleExifStripping = { enabled ->
                            userPrefs.setExifStrippingEnabled(enabled)
                            isExifStrippingEnabled = enabled
                        },
                        onToggleGalleryHide = { enabled ->
                            userPrefs.setGalleryHidden(enabled)
                            isGalleryHidden = enabled
                        },
                        onToggleAutoLock = { enabled ->
                            if (enabled && !isBiometricLockEnabled) {
                                backupStatusMessage = "자동 잠금을 사용하려면 먼저 생체 인식 잠금을 활성화하세요."
                            } else {
                                userPrefs.setAutoLockEnabled(enabled)
                                isAutoLockEnabled = enabled
                                backupStatusMessage = if (enabled) "백그라운드 전환 시 자동으로 잠깁니다." else "자동 잠금이 비활성화되었습니다."
                            }
                        },
                        onSaveApiKey = { key ->
                            val saved = apiKeyStore.saveGeminiApiKey(key)
                            hasApiKey = saved
                            backupStatusMessage = if (saved) "API Key를 저장했습니다." else "보안 저장소를 사용할 수 없어 API Key를 저장하지 못했습니다."
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
                        onImportLocalVlmModel = {
                            localVlmModelLauncher.launch(arrayOf("application/octet-stream", "application/x-mediapipe", "*/*"))
                        },
                        onDeleteLocalVlmModel = {
                            localVisionModelManager.clearModel()
                            localVlmModelName = null
                            hasLocalVlmModel = false
                            backupStatusMessage = context.getString(R.string.settings_local_vlm_deleted)
                        },
                        onExportBackup = { exportLauncher.launch("MarkScene_Backup_${System.currentTimeMillis()}.zip") },
                        onImportBackup = { importLauncher.launch(arrayOf("application/zip")) },
                        onExportCsv = { csvExportLauncher.launch("MarkScene_Data_${System.currentTimeMillis()}.csv") },
                        onExportMarkdown = { mdExportLauncher.launch("MarkScene_Records_${System.currentTimeMillis()}.md") },
                        onDeleteTagCorrection = { original -> scope.launch { database.tagCorrectionDao().delete(original) } },
                        externalMessage = backupStatusMessage,
                        onMessageShown = { backupStatusMessage = null },
                        onOpenPrivacyNotice = { navController.navigate(PRIVACY_ROUTE) },
                        onOpenPrivacyDashboard = { navController.navigate(PRIVACY_DASHBOARD_ROUTE) },
                        onOpenTutorial = {
                            val tutorialUri = Uri.parse("https://github.com/jeiel85/markscene-android#-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5")
                            val intent = Intent(Intent.ACTION_VIEW, tutorialUri)
                            context.startActivity(intent)
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(PRIVACY_ROUTE) {
                    PrivacyNoticeScreen(onBack = { navController.popBackStack() })
                }
                composable(PRIVACY_DASHBOARD_ROUTE) {
                    val tagCount = remember(allRecords) {
                        allRecords.flatMap { it.tags }.map { it.name }.distinct().size
                    }
                    PrivacyDashboardScreen(
                        recordCount = allRecords.size,
                        tagCount = tagCount,
                        hasApiKey = hasApiKey,
                        hasLocalVlmModel = hasLocalVlmModel,
                        isBiometricEnabled = isBiometricLockEnabled,
                        lastBackupDate = null,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
