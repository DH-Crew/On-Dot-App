package com.ondot.build_logic.convention

import com.ondot.build_logic.convention.internal.configureToolchains
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

/**
 * :domain, :core:platform, :composeApp 등 순수 android, jvm 모듈을 제외한 모든 kmp 모듈에 적용
 * 프로젝트 타깃, 빌드 산출물 설정
 * */
class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        configureToolchains()

        val libs = project.extensions
            .getByType<VersionCatalogsExtension>()
            .named("libs")

        extensions.configure<KotlinMultiplatformExtension> {
            applyDefaultHierarchyTemplate()
            val iosX64T = iosX64()
            val iosArm64T = iosArm64()
            val iosSimArm64T = iosSimulatorArm64()

            androidTarget {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            targets.withType<KotlinNativeTarget> {
                binaries.framework {
                    baseName = (findProperty("xcframework.basename") as String?) ?: project.name
                    isStatic = true
                }
            }

            sourceSets.apply {
                commonMain.dependencies {
                    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                    implementation(libs.findLibrary("kotlinx-serialization-json").get())
                    implementation(libs.findLibrary("ktor-client").get())
                    implementation(libs.findLibrary("kermit").get())
                }
                androidMain.dependencies {
                    implementation(libs.findLibrary("ktor-client-okhttp").get())
                }
                iosMain.dependencies {
                    implementation(libs.findLibrary("ktor-client-darwin").get())
                }
            }

            val xcFramework = XCFramework("ComposeApp")
            listOf("Debug","Release").forEach { bt ->
                listOf(iosArm64T, iosSimArm64T, iosX64T).forEach { t ->
                    xcFramework.add(t.binaries.getFramework(bt))
                }
            }

            tasks.register("packXCFramework") {
                dependsOn(
                    "linkDebugFrameworkIosArm64","linkDebugFrameworkIosSimulatorArm64","linkDebugFrameworkIosX64",
                    "linkReleaseFrameworkIosArm64","linkReleaseFrameworkIosSimulatorArm64","linkReleaseFrameworkIosX64"
                )
            }
        }
    }
}