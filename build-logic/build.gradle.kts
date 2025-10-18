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
        register("kmpAppConvention") {
            id = "convention.kmp.app"
            implementationClass = "com.ondot.build_logic.convention.KmpAppConventionPlugin"
        }

        register("composeMultiplatformConvention") {
            id = "convention.compose"
            implementationClass = "com.ondot.build_logic.convention.ComposeMultiplatformConventionPlugin"
        }

        register("kmpDomainConvention") {
            id = "convention.domain"
            implementationClass = "com.ondot.build_logic.convention.KmpDomainConventionPlugin"
        }

        register("composeLifecycleConvention") {
            id = "convention.compose.lifecycle"
            implementationClass = "com.ondot.build_logic.convention.ComposeLifecycleConventionPlugin"
        }

        register("composeKoinConvention") {
            id = "convention.koin"
            implementationClass = "com.ondot.build_logic.convention.ComposeKoinConventionPlugin"
        }

        register("androidLibraryConvention") {
            id = "convention.android.library"
            implementationClass = "com.ondot.build_logic.convention.AndroidLibraryConventionPlugin"
        }

        register("featureComposeConvention") {
            id = "convention.feature"
            implementationClass = "com.ondot.build_logic.convention.feature.FeatureComposeConventionPlugin"
        }

        register("kotlinMultiplatformConvention") {
            id = "convention.kmp"
            implementationClass = "com.ondot.build_logic.convention.KotlinMultiplatformConventionPlugin"
        }

        register("compose-app") {
            id = "ondot.compose.app"
            implementationClass = "com.ondot.build_logic.ComposeAppPlugin"
        }

        /**----------------------------Legacy----------------------------*/

        register("compose-multiplatform") {
            id = "ondot.compose.multiplatform"
            implementationClass = "com.ondot.build_logic.ComposeMultiplatformPlugin"
        }
    }
}