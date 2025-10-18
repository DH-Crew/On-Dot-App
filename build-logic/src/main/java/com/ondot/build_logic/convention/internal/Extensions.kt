package com.ondot.build_logic.convention.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.addComposeCommonDependencies(libs: VersionCatalog) {
    extensions.getByType<KotlinMultiplatformExtension>().apply {
        sourceSets.commonMain.dependencies {
            implementation(libs.findLibrary("compose-runtime").get())
            implementation(libs.findLibrary("compose-foundation").get())
            implementation(libs.findLibrary("compose-ui").get())
            implementation(libs.findLibrary("compose-material3").get())
            implementation(libs.findLibrary("compose-components-resources").get())
        }
    }
}

fun Project.applyOnce(id: String) {
    if (!plugins.hasPlugin(id)) pluginManager.apply(id)
}