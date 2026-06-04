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
           jvmTarget = JvmTarget.JVM_17
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
            // BOM de Firebase para resolver versiones en Android (Requerido por GitLive)
            implementation(project.dependencies.platform(libs.firebase.bom))
            // ktor para Android
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            // Driver de base de datos para iOS
            implementation(libs.sqldelight.native)
            // ktor para iOS
            implementation(libs.ktor.client.darwin)
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

            // Navegación Voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)

            // Persistencia y Utils
            implementation(libs.sqldelight.runtime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.voyager.koin)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.compose)

            // Para los iconos (Search, Star, etc.)
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

sqldelight {
    databases {
        create("ByMeDatabase") {
            packageName.set("com.byme.app.db")
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
