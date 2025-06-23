package com.ondot.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpComposeUiPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        pluginManager.apply(KmpCommonPlugin::class.java)

        val libs = libsCatalogOrNull()?.named("libs")
            ?: error("libs version catalog not found")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                getByName("commonMain").dependencies {
                    implementation(libs.findLibrary("compose.runtime").get())
                    implementation(libs.findLibrary("compose.foundation").get())
                    implementation(libs.findLibrary("compose.material3").get())
                    implementation(libs.findLibrary("compose.ui").get())
                    implementation(libs.findLibrary("compose.components.resources").get())
                    implementation(libs.findLibrary("androidx.lifecycle.viewmodel").get())
                    implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                }

                getByName("androidMain").dependencies {
                    implementation(libs.findLibrary("androidx.activity.compose").get())
                    implementation(libs.findLibrary("compose.ui.tooling.preview").get())
                }
            }
        }

        dependencies {
            "debugImplementation"(libs.findLibrary("compose.uiTooling").get())
        }
    }
}
