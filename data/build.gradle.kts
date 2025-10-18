import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("convention.android.library")
    id("convention.kmp")
    id("convention.koin")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.extensions)

            implementation(projects.domain)
            implementation(projects.core.network)
        }

        androidMain.dependencies {
            implementation(libs.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.native.driver)
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.all {
            linkerOpts("-lsqlite3")
        }
    }
}

sqldelight {
    databases {
        create("OndotDatabase") {
            packageName.set("com.dh.ondot.data.local.db")
        }
    }
}