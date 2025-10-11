package com.ondot.build_logic.convention

import com.android.build.api.dsl.ApplicationExtension
import com.ondot.build_logic.convention.internal.applyAppDefaults
import com.ondot.build_logic.convention.internal.computeNamespace
import com.ondot.build_logic.convention.internal.configureAndroid
import com.ondot.build_logic.convention.internal.configureToolchains
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("com.android.application")

        val properties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }
        val kakaoKey = properties.getProperty("KAKAO_APP_KEY") ?: error("KAKAO_APP_KEY not found in local.properties")

        configureToolchains()

        extensions.configure<ApplicationExtension> {
            namespace = computeNamespace()
            configureAndroid(this@with)
            applyAppDefaults(this@with)

            buildFeatures { buildConfig = true }

            packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"

            buildTypes.getByName("release").isMinifyEnabled = false

            defaultConfig {
                versionCode = (findProperty("version.code") as String?)?.toInt() ?: 1
                versionName = (findProperty("version.name") as String?) ?: "1.0.0"

                buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
                manifestPlaceholders["KAKAO_HOST_SCHEME"] = "kakao$kakaoKey"
            }
        }
    }
}