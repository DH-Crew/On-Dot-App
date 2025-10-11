package com.ondot.build_logic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

/**
 * :domain, :core:platform, :composeApp 등 순수 android, jvm 모듈을 제외한 모든 kmp 모듈에 적용
 * 프로젝트 타깃, 빌드 산출물 설정
 * */
class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        extensions.configure<KotlinMultiplatformExtension> {
            androidTarget {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            val iosArm64Target = iosArm64()
            val iosSimArm64Target = iosSimulatorArm64()

            // iOS 프레임워크 설정
            listOf(iosArm64Target, iosSimArm64Target).forEach { target ->
                target.binaries.framework {
                    baseName = "ComposeApp"
                    isStatic = true
                    freeCompilerArgs += listOf(
                        "-Xbinary=bundleId=com.dh.ondot"
                    )
                }
            }

            val xcFramework = XCFramework("ComposeApp")
            xcFramework.add(iosArm64Target.binaries.getFramework("DEBUG"))
            xcFramework.add(iosSimArm64Target.binaries.getFramework("DEBUG"))
        }
    }
}