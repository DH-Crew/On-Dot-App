package com.ondot.build_logic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Compose Lifecycle 의존성이 필요한 모듈에 적용
 * */
class ComposeLifecycleConventionPlugin: Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val libs = extensions
            .getByType<VersionCatalogsExtension>()
            .named("libs")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(libs.findLibrary("lifecycleViewModel").get())
                implementation(libs.findLibrary("lifecycleViewModelCompose").get())
                implementation(libs.findLibrary("lifecycleRuntimeCompose").get())
            }
        }
    }
}