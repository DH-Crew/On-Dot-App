plugins {
    alias(libs.plugins.androidLibrary)
    id("ondot.kotlin.multiplatform")
}

kotlin {
    android {
        namespace = "com.ondot.core"
        compileSdk = 36
    }
}