package com.ondot.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension


class KotlinMultiplatformPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")

        extensions.configure<KotlinMultiplatformExtension> {
            androidTarget {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            // iOS 프레임워크 설정
            listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
                target.binaries.framework {
                    baseName = "ComposeApp"
                    isStatic = true
                    freeCompilerArgs += listOf(
                        "-Xbinary=bundleId=com.dh.ondot"
                    )
                }
            }
        }
    }
}