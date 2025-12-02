package com.ondot.build_logic.convention.feature

import com.ondot.build_logic.convention.internal.applyOnce
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FeatureComposeConventionPlugin: Plugin<Project> {
    override fun apply(project: Project)= with(project) {
        applyOnce("convention.android.library")
        applyOnce("convention.kmp")
        applyOnce("convention.compose")
        applyOnce("convention.koin")
        applyOnce("convention.compose.lifecycle")

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(project(":domain"))
                implementation(project(":core:ui"))
                implementation(project(":core:bridge"))
                implementation(project(":core:navigation"))
                implementation(project(":core:platform"))
                implementation(project(":core:util"))
                implementation(project(":core:design-system"))

                implementation(libs.findLibrary("navigation").get())
            }

            sourceSets.commonTest.dependencies {
                implementation(kotlin("test"))
                implementation(libs.findLibrary("coroutines-test").get())
            }
        }
    }
}