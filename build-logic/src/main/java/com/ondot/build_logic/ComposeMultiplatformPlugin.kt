package com.ondot.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1) 필요한 플러그인 적용
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.compose")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        // 2) 버전 카탈로그 읽기
        val libs = project.extensions
            .findByType<VersionCatalogsExtension>()!!
            .named("libs")

        // 3) KMP sourceSets에 의존성 추가
        project.extensions.configure<KotlinMultiplatformExtension> {
            with(sourceSets) {
                val commonMain = maybeCreate("commonMain")
                commonMain.resources.srcDirs("src/commonMain/resources")
                commonMain.dependencies {
                    implementation(libs.findLibrary("compose-runtime").get())
                    implementation(libs.findLibrary("compose-foundation").get())
                    implementation(libs.findLibrary("compose-ui").get())
                    implementation(libs.findLibrary("compose-material3").get())
                    implementation(libs.findLibrary("compose-components-resources").get())
                    implementation(libs.findLibrary("kermit").get())
                    implementation(libs.findLibrary("kotlinx-serialization-json").get())
                    implementation(libs.findLibrary("ktor-client").get())
                    implementation(libs.findLibrary("ktor-client-content-negotiation").get())
                    implementation(libs.findLibrary("ktor-serialization").get())
                    implementation(libs.findLibrary("ktor-client-logging").get())
                    implementation(libs.findLibrary("viewmodel").get())
                    implementation(libs.findLibrary("navigation").get())
                    implementation(libs.findLibrary("compottie").get())
                    implementation(libs.findLibrary("compottie-dot").get())
                    implementation(libs.findLibrary("compottie-network").get())
                    implementation(libs.findLibrary("kotlinx-io-core").get())
                    implementation(libs.findLibrary("kotlinx-io-bytestring").get())
                    implementation(libs.findLibrary("kotlinx-datetime").get())
                    implementation(libs.findLibrary("coroutines-extensions").get())
                    implementation("io.insert-koin:koin-core:4.0.3")
                }

                val androidMain = maybeCreate("androidMain")
                androidMain.dependencies {
                    implementation(libs.findLibrary("androidx-activity-compose").get())
                    implementation(libs.findLibrary("compose-ui-tooling-preview").get())
                    implementation(libs.findLibrary("kotlinx-coroutines-android").get())
                    implementation(libs.findLibrary("androidx-lifecycle-viewmodel").get())
                    implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
                    implementation(libs.findLibrary("datastore-core").get())
                    implementation(libs.findLibrary("datastore-preferences").get())
                    implementation(libs.findLibrary("ktor-client-okhttp").get())
                    implementation(libs.findLibrary("kakao-login").get())
                    implementation(libs.findLibrary("android-driver").get())
                    implementation(libs.findLibrary("firebase-analytics").get())
                    implementation("io.insert-koin:koin-android:4.0.3")
                }

                val iosMain = maybeCreate("iosMain")
                iosMain.dependencies {
                    implementation(libs.findLibrary("ktor-client-darwin").get())
                    implementation(libs.findLibrary("native-driver").get())
                }
            }
        }

        project.configurations.configureEach {
            resolutionStrategy.eachDependency {
                when {
                    requested.group == "io.ktor" -> {
                        useVersion("3.0.0")
                    }
                }
            }
        }

        project.dependencies.add(
            "debugImplementation",
            libs.findLibrary("compose-uiTooling").get()
        )
    }
}
