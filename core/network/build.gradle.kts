plugins {
    id("convention.android.library")
    id("convention.kmp")
    id("convention.koin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
            implementation(projects.core.platform)
        }
    }
}