package com.ondot.build_logic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Ktor 의존성이 필요한 모듈에 적용
 * */
class KtorConventionPlugin: Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val libs = extensions
            .getByType<VersionCatalogsExtension>()
            .named("libs")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(libs.findLibrary("ktor-client").get())
            }
            sourceSets.androidMain.dependencies {
                implementation(libs.findLibrary("ktor-client-okhttp").get())
            }
            sourceSets.iosMain.dependencies {
                implementation(libs.findLibrary("ktor-client-darwin").get())
            }
        }
    }
}