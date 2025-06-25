package com.ondot.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class ComposeAppPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(AndroidApplicationPlugin::class.java)
        project.pluginManager.apply(KotlinMultiplatformPlugin::class.java)
        project.pluginManager.apply(ComposeMultiplatformPlugin::class.java)
    }
}
