package com.ondot.build_logic

import com.ondot.build_logic.convention.ComposeMultiplatformConventionPlugin
import com.ondot.build_logic.convention.KmpAppConventionPlugin
import com.ondot.build_logic.convention.KotlinMultiplatformConventionPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposeAppPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply(KmpAppConventionPlugin::class.java)
        pluginManager.apply(KotlinMultiplatformConventionPlugin::class.java)
        pluginManager.apply(ComposeMultiplatformConventionPlugin::class.java)
    }
}
