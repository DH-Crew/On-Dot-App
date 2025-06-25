package com.ondot.build_logic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("com.android.application")

        val libs = extensions.findByType<VersionCatalogsExtension>()!!.named("libs")

        extensions.configure<ApplicationExtension> {
            namespace = "com.dh.ondot"
            compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()

            defaultConfig {
                applicationId = "com.dh.ondot"
                minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                targetSdk = libs.findVersion("android-targetSdk").get().requiredVersion.toInt()
                versionCode = 1
                versionName = "1.0"
            }

            packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"

            buildTypes.getByName("release").isMinifyEnabled = false

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}
