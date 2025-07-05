
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("ondot.compose.app")
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
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
        }
    }
}