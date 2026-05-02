import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.markscene.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.markscene.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 200
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // 우선순위: 1. 환경 변수 (CI), 2. local.properties (로컬)
            val envStoreFile = System.getenv("RELEASE_STORE_FILE")
            val envStorePassword = System.getenv("RELEASE_STORE_PASSWORD")
            val envKeyAlias = System.getenv("RELEASE_KEY_ALIAS")
            val envKeyPassword = System.getenv("RELEASE_KEY_PASSWORD")

            if (!envStoreFile.isNullOrEmpty()) {
                storeFile = file(envStoreFile)
                storePassword = envStorePassword
                keyAlias = envKeyAlias
                keyPassword = envKeyPassword
            } else {
                val propertiesFile = rootProject.file("local.properties")
                if (propertiesFile.exists()) {
                    val properties = Properties()
                    propertiesFile.inputStream().use { properties.load(it) }
                    storeFile = file(properties.getProperty("RELEASE_STORE_FILE", ""))
                    storePassword = properties.getProperty("RELEASE_STORE_PASSWORD", "")
                    keyAlias = properties.getProperty("RELEASE_KEY_ALIAS", "")
                    keyPassword = properties.getProperty("RELEASE_KEY_PASSWORD", "")
                }
            }
        }
        getByName("debug") {
            // 프로젝트 내 고정 디버그 키스토어 사용 (CI와 로컬 동일한 서명)
            storeFile = rootProject.file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        // debug: 기본 Android debug.keystore 사용 (~/.android/debug.keystore)
        debug {}
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.android.material)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.biometric)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.coil.compose)
    implementation(libs.mlkit.image.labeling)
    implementation(libs.mlkit.text.recognition.korean)
    implementation(libs.okhttp)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
