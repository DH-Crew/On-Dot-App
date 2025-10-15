package com.ondot.build_logic.convention

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.ondot.build_logic.convention.internal.applyAppDefaults
import com.ondot.build_logic.convention.internal.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import java.util.Properties

class KmpAppConventionPlugin: Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.android.application")

        val properties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }
        val kakaoKey = properties.getProperty("KAKAO_APP_KEY") ?: error("KAKAO_APP_KEY not found in local.properties")

        extensions.getByType<BaseAppModuleExtension>().apply {
            namespace = "com.dh.ondot"
            configureAndroid(this@with)
            applyAppDefaults(this@with)

            buildFeatures {
                buildConfig = true
            }

            defaultConfig {
                applicationId = "com.dh.ondot"

                versionCode = (findProperty("version.code") as String?)?.toInt() ?: 1
                versionName = (findProperty("version.name") as String?) ?: "1.0.0"

                buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
                manifestPlaceholders["KAKAO_HOST_SCHEME"] = "kakao$kakaoKey"
            }

            buildTypes {
                getByName("release") {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        file("proguard-rules.pro")
                    )
                }
            }

            packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
        }
    }
}