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
            implementation(projects.core.result)

            implementation(libs.ktor.client)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.client.logging)
        }
    }
}
