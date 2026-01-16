plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
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
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.preview)
            }
        }
        androidMain {
            dependencies {
            }
        }
        jvmMain {
            dependencies {
                api(compose.preview)
            }
        }
    }
}
