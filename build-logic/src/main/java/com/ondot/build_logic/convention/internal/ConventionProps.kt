package com.ondot.build_logic.convention.internal

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * gradle.properties 값을 꺼내는 유틸 메서드
 * @param name 속성 이름
 * @param default 기본값
 * property 가 있으면 반환하고 없으면 default 반환
 * */
fun Project.intProp(name: String, default: Int) =
    providers.gradleProperty(name).map(String::toInt).orElse(default)
fun Project.boolProp(name: String, default: Boolean) =
    providers.gradleProperty(name).map { it.toBoolean() }.orElse(default)
fun Project.strProp(name: String, default: String) =
    providers.gradleProperty(name).orElse(default)

/**
 * Java/Kotlin/JVM 컴파일에 사용할 JDK 17 툴체인을 모듈별 플러그인에 맞춰 일괄 고정
 * */
fun Project.configureToolchains() {
    // Gradle Java Toolchain (AGP/Java 플러그인이 인식)
    extensions.findByType(JavaPluginExtension::class.java)?.toolchain?.languageVersion
        ?.set(JavaLanguageVersion.of(17))

    // Kotlin JVM(안드로이드/순수 JVM 모듈)
    extensions.findByType(KotlinJvmProjectExtension::class.java)
        ?.jvmToolchain(17)

    // Kotlin Multiplatform (공통적으로 JVM toolchain 17 사용)
    extensions.findByType(KotlinMultiplatformExtension::class.java)
        ?.jvmToolchain(17)
}