package com.ondot.build_logic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * :domain 모듈에 적용할 convention
 * */
class KmpDomainConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        val libs = project.extensions
            .getByType<VersionCatalogsExtension>()
            .named("libs")

        extensions.configure<KotlinMultiplatformExtension> {
            jvm {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
            iosX64(); iosArm64(); iosSimulatorArm64()

            sourceSets.apply {
                commonMain {
                    dependencies {
                        implementation(libs.findLibrary("kotlinx-serialization-core").get())
                        implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                    }
                }
            }
        }
    }
}