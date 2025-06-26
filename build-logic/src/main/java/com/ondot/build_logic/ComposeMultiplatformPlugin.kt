package com.ondot.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1) 필요한 플러그인 적용
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.compose")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

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
                }
                
                val iosMain = maybeCreate("iosMain")
                iosMain.dependencies {
                    implementation(libs.findLibrary("ktor-client-darwin").get())
                }
            }
        }

        project.configurations.configureEach {
            resolutionStrategy.eachDependency {
                if (requested.group == "org.jetbrains.kotlinx" &&
                    requested.name  == "kotlinx-io-core") {
                    useVersion("0.3.3")
                    because("Align io-core with Ktor to avoid ByteReadPacket symbol clash")
                }
                if (requested.group == "org.jetbrains.kotlinx" &&
                    requested.name == "kotlinx-io-bytestring") {
                    useVersion("0.3.3")
                }
            }
        }

        project.configurations.configureEach {
            resolutionStrategy.eachDependency {
                if (requested.group == "io.ktor") useVersion("2.3.10")
            }
        }

        project.dependencies.add(
            "debugImplementation",
            libs.findLibrary("compose-uiTooling").get()
        )
    }
}
