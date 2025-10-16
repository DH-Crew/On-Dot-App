import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Properties
import kotlin.apply

plugins {
    id("convention.android.library")
    id("convention.kmp")
    id("convention.compose")
    id("convention.compose.lifecycle")
    id("convention.koin")
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildKonfig)
}

buildkonfig {
    packageName = "com.dh.ondot"

    exposeObjectWithName = "BuildKonfig"

    val props = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) file.inputStream().use { load(it) }
    }
    val baseUrl = props.getProperty("BASE_URL")

    defaultConfigs {
        buildConfigField(
            Type.STRING,
            "BASE_URL",
            baseUrl
        )
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.client.logging)

            implementation(projects.domain)
            implementation(projects.core.util)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)

            implementation(libs.android.driver)

            implementation(libs.datastore.core)
            implementation(libs.datastore.preferences)

            implementation(libs.androidx.activity.compose)

            implementation(libs.firebase.analytics)

            implementation(libs.kakao.login)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)

            implementation(libs.native.driver)
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

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.all {
            linkerOpts("-lsqlite3")
        }
    }
}

sqldelight {
    databases {
        create("OndotDatabase") {
            packageName.set("com.dh.ondot.data.local.db")
        }
    }
}