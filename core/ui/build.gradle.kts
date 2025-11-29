plugins {
    id("convention.android.library")
    id("convention.kmp")
    id("convention.compose")
    id("convention.compose.lifecycle")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
            implementation(projects.core.designSystem)
        }
    }
}