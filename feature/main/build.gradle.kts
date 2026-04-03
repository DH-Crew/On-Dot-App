plugins {
    id("convention.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.calendar)
        }
    }
}
