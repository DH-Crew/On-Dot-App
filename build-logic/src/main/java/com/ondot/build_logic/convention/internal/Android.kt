package com.ondot.build_logic.convention.internal

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

/**
 * AGP 의 namespace 값을 자동 생성하는 메서드
 * */
fun Project.computeNamespace(): String {
    val base = strProp("namespace.base", "com.dh").get()
    val raw = path.trim(':').replace(':', '.').ifBlank { rootProject.name }
    val sanitized = raw.replace(Regex("[^A-Za-z0-9_.]"), "_")
    return "$base.$sanitized"
}

/**
 * 공통 Android 기본값 설정
 * */
fun <T: CommonExtension<*, *, *, *, *, *>> T.configureAndroid(project: Project) {
    compileSdk = project.intProp("compile.sdk", 36).get()

    defaultConfig {
        minSdk = project.intProp("min.sdk", 29).get()
        vectorDrawables { useSupportLibrary = true }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/AL2.0", "META-INF/LGPL2.1",
                "META-INF/LICENSE*", "META-INF/NOTICE*"
            )
        )
    }
    lint {
        checkReleaseBuilds = true
        warningsAsErrors = true
        abortOnError = true
    }
}

/**
 * 앱 모듈 전용 설정
 * */
fun ApplicationExtension.applyAppDefaults(project: Project) {
    defaultConfig {
        targetSdk = project.intProp("target.sdk", 36).get()
    }
}