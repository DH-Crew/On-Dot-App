import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("ondot.compose.app")
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    cocoapods {
        version = "1.0.0"

        summary = "Compose Multiplatform Shared UI"
        homepage = "https://github.com/DH-Crew/On-Dot-App"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "composeApp"
            isStatic = true
            binaryOption("bundleId", "com.dh.ondot")
        }
    }
}
