plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    google()
    mavenCentral()
    // KMP plugin 인식을 위해 필요함
    gradlePluginPortal()
}

dependencies {
    // Kotlin Multiplatform 플러그인 사용을 위한 의존성
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("android-application") {
            id = "ondot.android.application"
            implementationClass = "com.ondot.build_logic.convention.AndroidApplicationConventionPlugin"
        }
        register("compose-app") {
            id = "ondot.compose.app"
            implementationClass = "com.ondot.build_logic.ComposeAppPlugin"
        }
        register("compose-multiplatform") {
            id = "ondot.compose.multiplatform"
            implementationClass = "com.ondot.build_logic.ComposeMultiplatformPlugin"
        }
        register("kotlin-multiplatform") {
            id = "ondot.kotlin.multiplatform"
            implementationClass = "com.ondot.build_logic.convention.KotlinMultiplatformConventionPlugin"
        }

        register("compose-multiplatform-convention") {
            id = "ondot.compose.multiplatform.convention"
            implementationClass = "com.ondot.build_logic.convention.ComposeMultiplatformConventionPlugin"
        }
    }
}