plugins {
    id("convention.android.library")
    id("convention.kmp")
    id("convention.compose")
    id("convention.compose.lifecycle")
    id("convention.koin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            implementation(projects.core.util)
        }
    }
}