package com.ondot.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.creating
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
                commonMain.dependencies {
                    implementation(libs.findLibrary("compose-runtime").get())
                    implementation(libs.findLibrary("compose-foundation").get())
                    implementation(libs.findLibrary("compose-ui").get())
                    implementation(libs.findLibrary("compose-material3").get())
                    implementation(libs.findLibrary("compose-components-resources").get())
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
                }
            }
        }

        // 4) ui-tooling debugImplementation 으로 추가
        project.dependencies.add(
            "debugImplementation",
            libs.findLibrary("compose-uiTooling").get()
        )
    }
}
