import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    androidLibrary {
       namespace = "com.byme.app.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            // Driver de base de datos para Android
            implementation(libs.sqldelight.android)
        }
        iosMain.dependencies {
            // Driver de base de datos para iOS
            implementation(libs.sqldelight.native)
        }
        commonMain.dependencies {
            // UI Compartida
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            // Firebase Multiplatform (GitLive)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)

            // Navegación Voyager (Punto 4 de la guía)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)

            // Persistencia y Utils (Punto 3 de la guía)
            implementation(libs.sqldelight.runtime)
            implementation(libs.kotlinx.serialization.json)
        }

        val androidMain by getting {
            dependencies {
                // ESTA LÍNEA ES LA SOLUCIÓN AL ERROR:
                // Le da versiones reales a las librerías de Google que pide GitLive
                implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.5.1"))
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}


dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}