pluginManagement {
    repositories {
        google()            // <-- needed for com.android.application
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()            // <-- Android libraries
        mavenCentral()
    }
}

rootProject.name = "AIFileTransferApp_OfflineReady"
include(":app")
