package com.ondot.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project

// 대부분의 KMP 모듈에 추가해야 하는 공통 플러그인
// 순수 Android 모듈이거나 KMP가 필요없는 모듈에는 추가하지 않아도 됨
class KmpCommonPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        plugins.apply("org.jetbrains.kotlin.multiplatform")
        configureKMPCommonPlugin()
    }
}
