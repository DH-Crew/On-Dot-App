
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Properties
import kotlin.apply

plugins {
    id("ondot.compose.app")
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.google.services)
    alias(libs.plugins.buildKonfig)
}

buildkonfig {
    packageName = "com.dh.ondot.composeApp"

    exposeObjectWithName = "BuildKonfig"

    val props = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) file.inputStream().use { load(it) }
    }
    val baseUrl = props.getProperty("BASE_URL")
    val amplitudeKey = props.getProperty("AMPLITUDE_KEY")

    defaultConfigs {
        buildConfigField(
            Type.STRING,
            "BASE_URL",
            baseUrl
        )
        buildConfigField(
            Type.STRING,
            "AMPLITUDE_KEY",
            amplitudeKey
        )
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
            implementation(projects.data)
            implementation(projects.core.platform)
            implementation(projects.core.network)
            implementation(projects.core.ui)
            implementation(projects.core.util)
            implementation(projects.core.navigation)
            implementation(projects.core.designSystem)
            implementation(projects.feature.alarm)
            implementation(projects.feature.edit)
            implementation(projects.feature.general)
            implementation(projects.feature.login)
            implementation(projects.feature.main)
            implementation(projects.feature.onboarding)
            implementation(projects.feature.splash)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            implementation(libs.kotlinx.coroutines.android)

            implementation(libs.datastore.core)
            implementation(libs.datastore.preferences)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.kakao.login)

            implementation(libs.firebase.analytics)

            implementation(libs.amplitude.analytics)
            implementation(libs.amplitude.session.replay)

        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }

    targets.withType<KotlinNativeTarget> {
        compilations["main"].cinterops {
            create("KakaoLoginHelper") {
                // def 파일 위치
                definitionFile = project.file("src/iosMain/nativeInterop/cinterop/KakaoLoginHelper.def")

                // 헤더가 있는 경로 (iosApp 루트)
                includeDirs.allHeaders(project.rootDir.resolve("iosApp/iosApp"))
                includeDirs.allHeaders(project.rootDir.resolve("iosApp/ThirdParty/KakaoSDKAuth/Headers"))
            }

            create("AnalyticsBridge") {
                definitionFile = project.file("src/iosMain/nativeInterop/cinterop/AnalyticsBridge.def")
                // 헤더가 있는 경로 (iosApp의 iOS 타깃 소스 루트)
                includeDirs.allHeaders(project.rootDir.resolve("iosApp/iosApp/AnalyticsBridge"))
            }

            create("AlarmKitBridge") {
                definitionFile = project.file("src/iosMain/nativeInterop/cinterop/AlarmKitBridge.def")
                // 헤더가 있는 경로 (iosApp의 iOS 타깃 소스 루트)
                includeDirs.allHeaders(project.rootDir.resolve("iosApp/iosApp/AlarmKitBridge"))
            }
        }
    }
}