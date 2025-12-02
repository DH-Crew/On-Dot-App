plugins {
    id("convention.android.library")
    id("convention.kmp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
        }
    }
}