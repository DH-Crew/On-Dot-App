package com.ondot.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class ComposeAppPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply<AndroidApplicationPlugin>()
        apply<KmpComposeUiPlugin>()
    }
}
