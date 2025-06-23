package com.ondot.build_logic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKMPCommonPlugin() {
    extensions.configure<KotlinMultiplatformExtension> {
        androidTarget()
        iosX64()
        iosArm64()
        iosSimulatorArm64()

        val libs = libsCatalogOrNull()?.named("libs")
            ?: error("libs version catalog not found")
        with(sourceSets) {
            val commonMain = getByName("commonMain")
            commonMain.dependencies {
                implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                implementation(libs.findLibrary("kotlinx.serialization.json").get())
                implementation(libs.findLibrary("kotlinx.datetime").get())
                implementation(libs.findLibrary("napier").get())
                implementation(libs.findLibrary("compose.runtime").get())
            }

            val commonTest = getByName("commonTest")
            commonTest.dependencies {
                implementation(kotlin("test"))
            }

            val androidMain = getByName("androidMain")
            val iosX64Main = getByName("iosX64Main")
            val iosArm64Main = getByName("iosArm64Main")
            val iosSimulatorArm64Main = getByName("iosSimulatorArm64Main")

            val iosMain = create("iosMain") {
                dependsOn(commonMain)
                iosX64Main.dependsOn(this)
                iosArm64Main.dependsOn(this)
                iosSimulatorArm64Main.dependsOn(this)
            }
        }
    }
}

internal fun Project.libsCatalogOrNull(): VersionCatalogsExtension? =
    extensions.findByType<VersionCatalogsExtension>()