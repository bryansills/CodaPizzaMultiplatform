plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.bignerdranch.codapizza.core"
        compileSdk = 36
        minSdk = 24
        androidResources.enable = true
        compilerOptions {
            freeCompilerArgs.addAll("-P", "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.bignerdranch.codapizza.core.Parcelize")
        }
    }
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.ui.tooling.core)
                implementation(libs.jetbrains.compose.ui.tooling.preview)
                implementation(libs.androidx.material3)
                implementation(libs.androidx.navigation3.ui)
                implementation(libs.androidx.navigation3.runtime)
                implementation(libs.kotlinx.serialization.core)
            }
        }
    }
}
