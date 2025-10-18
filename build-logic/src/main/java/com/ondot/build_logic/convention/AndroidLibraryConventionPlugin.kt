package com.ondot.build_logic.convention

import com.android.build.gradle.LibraryExtension
import com.ondot.build_logic.convention.internal.computeNamespace
import com.ondot.build_logic.convention.internal.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.android.library")

        extensions.getByType<LibraryExtension>().apply {
            namespace = computeNamespace()
            configureAndroid(this@with)

            buildFeatures {
                buildConfig = true
            }
        }
    }
}