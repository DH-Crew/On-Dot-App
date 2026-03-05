package com.ondot.build_logic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")
    }
}
