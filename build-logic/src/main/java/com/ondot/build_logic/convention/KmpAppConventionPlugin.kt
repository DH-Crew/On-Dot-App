package com.ondot.build_logic.convention

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.ondot.build_logic.convention.internal.applyAppDefaults
import com.ondot.build_logic.convention.internal.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import java.util.Properties

class KmpAppConventionPlugin: Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.android.application")
        apply<KtlintConventionPlugin>()

        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { properties.load(it) }
        }

        val kakaoKey =
            properties.getProperty("KAKAO_APP_KEY")
                ?: System.getenv("KAKAO_APP_KEY")
                ?: error("KAKAO_APP_KEY not found in local.properties")
        val baseUrl =
            properties.getProperty("BASE_URL")
                ?: System.getenv("BASE_URL")
                ?: error("BASE_URL이 존재하지 않습니다.")
        val amplitudeKey =
            properties.getProperty("AMPLITUDE_KEY")
                ?: System.getenv("AMPLITUDE_KEY")
                ?: error("AMPLITUDE_KEY이 존재하지 않습니다.")

        val keystorePath =
            properties.getProperty("ANDROID_KEYSTORE_PATH")
                ?: System.getenv("ANDROID_KEYSTORE_PATH")

        val keystorePassword =
            properties.getProperty("ANDROID_KEYSTORE_PASSWORD")
                ?: System.getenv("ANDROID_KEYSTORE_PASSWORD")

        val keyAlias =
            properties.getProperty("ANDROID_KEY_ALIAS")
                ?: System.getenv("ANDROID_KEY_ALIAS")

        val keyPassword =
            properties.getProperty("ANDROID_KEY_PASSWORD")
                ?: System.getenv("ANDROID_KEY_PASSWORD")

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
                buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
                buildConfigField("String", "AMPLITUDE_KEY", "\"$amplitudeKey\"")
                manifestPlaceholders["KAKAO_HOST_SCHEME"] = "kakao$kakaoKey"
            }

            signingConfigs {
                create("release") {
                    storeFile = file(keystorePath)
                    storePassword = keystorePassword
                    this.keyAlias = keyAlias
                    this.keyPassword = keyPassword
                }
            }

            buildTypes {
                getByName("release") {
                    signingConfig = signingConfigs.getByName("release")
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