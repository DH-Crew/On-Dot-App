package com.ondot.build_logic.convention

import com.ondot.build_logic.convention.internal.addComposeCommonDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * 공통 UI 코드가 존재하는 모듈에 적용
 * 공통 UI 의존성
 * Compose Compiler Metric 토글
 * */
class ComposeMultiplatformConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        pluginManager.apply(KtorConventionPlugin::class.java)

        val libs = project.extensions
            .findByType<VersionCatalogsExtension>()!!
            .named("libs")

        /**
         * Compose Compiler 리포트
         * 성능, 리컴포지션 이슈 분석, 안정성 확인이 필요할 때 사용
         * gradle.properties에 compose.metrics.enabled=true 를 추가하면, 컴파일러 리포트가 생성됨
         * build/compose-reports/ 경로로 생성됨
         * 평소에는 false 로 두다가 필요할 때만 true 로 변경하는 것이 빌드 속도에 유리함
         * */
        val metrics = (findProperty("compose.metrics.enabled") as String?) == "true"
        if (metrics) {
            val out = layout.buildDirectory.dir("compose-reports").get().asFile
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-P","plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$out",
                        "-P","plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$out",
                    )
                }
            }
        }

        addComposeCommonDependencies(libs)

        // TODO(이후에 feature 모듈로 분리되어야 함)
        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain.dependencies {
                    implementation(libs.findLibrary("lifecycleViewModel").get())
                    implementation(libs.findLibrary("lifecycleViewModelCompose").get())
                    implementation(libs.findLibrary("lifecycleRuntimeCompose").get())

                    implementation(libs.findLibrary("navigation").get())

                    implementation(libs.findLibrary("koinCore").get())
                    implementation(libs.findLibrary("koinCompose").get())
                    implementation(libs.findLibrary("koinComposeViewModel").get())

                    implementation(libs.findLibrary("kermit").get())

                    implementation(libs.findLibrary("kotlinx-serialization-json").get())

                    implementation(libs.findLibrary("ktor-client-content-negotiation").get())
                    implementation(libs.findLibrary("ktor-serialization").get())
                    implementation(libs.findLibrary("ktor-client-logging").get())

                    implementation(libs.findLibrary("compottie").get())
                    implementation(libs.findLibrary("compottie-dot").get())
                    implementation(libs.findLibrary("compottie-network").get())

                    implementation(libs.findLibrary("kotlinx-io-core").get())
                    implementation(libs.findLibrary("kotlinx-io-bytestring").get())

                    implementation(libs.findLibrary("kotlinx-datetime").get())

                    implementation(libs.findLibrary("coroutines-extensions").get())
                }
            }
        }
    }
}