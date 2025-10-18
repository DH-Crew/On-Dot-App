
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("ondot.compose.app")
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.google.services)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
            implementation(projects.data)
            implementation(projects.core.platform)
            implementation(projects.core.network)
            implementation(projects.core.util)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            implementation(libs.kotlinx.coroutines.android)

            implementation(libs.datastore.core)
            implementation(libs.datastore.preferences)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.kakao.login)

            implementation(libs.firebase.analytics)
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