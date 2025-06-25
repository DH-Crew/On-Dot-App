import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("ondot.compose.app")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

val syncFramework = tasks.register<Sync>("syncFramework") {
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = if (sdkName.startsWith("iphoneos")) "iosArm64" else "iosX64"

    val nativeTarget = kotlin.targets.getByName(targetName) as KotlinNativeTarget
    val framework = nativeTarget.binaries.getFramework(mode)

    dependsOn(framework.linkTaskProvider)
    from({ framework.outputDirectory })
    into(layout.buildDirectory.dir("xcode-frameworks"))
}