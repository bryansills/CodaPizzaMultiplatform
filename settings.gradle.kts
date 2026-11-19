pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    val androidGradlePluginVersion = "9.5.1"
    val kotlinGradlePluginVersion = "2.4.21"

    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.android.application") version androidGradlePluginVersion apply false
    id("com.android.library") version androidGradlePluginVersion apply false
    id("com.android.kotlin.multiplatform.library") version androidGradlePluginVersion apply false
    id("org.jetbrains.compose") version "1.12.2" apply false
    id("org.jetbrains.kotlin.jvm") version kotlinGradlePluginVersion apply false
    id("org.jetbrains.kotlin.multiplatform") version kotlinGradlePluginVersion apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinGradlePluginVersion apply false
    id("org.jetbrains.kotlin.plugin.compose") version kotlinGradlePluginVersion apply false
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Coda Pizza Multiplatform"
include(":app")
include(":core")
include(":desktop")
