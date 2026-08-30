plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    android {
        namespace = "com.bignerdranch.codapizza.core"
        compileSdk = 37
        minSdk = 24
        androidResources.enable = true
    }
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.ui.tooling.core)
                implementation(libs.jetbrains.compose.ui.tooling.preview)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.navigation3.ui)
                implementation(libs.androidx.navigation3.runtime)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.jetbrains.viewmodel.navigation3)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}
