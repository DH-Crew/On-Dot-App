rootProject.name = "OnDot"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
        mavenLocal()
        flatDir {
            dirs("libs")
        }

    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
        mavenLocal()
        flatDir {
            dirs("libs")
        }

    }
}

include(":composeApp")
include(":domain")
include(":core")
include(":data")
include(":core:network")
