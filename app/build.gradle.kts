plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.simats.smartcart"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.simats.smartcart"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

tasks.register("adbReverse") {
    doLast {
        try {
            val localAppData = System.getenv("LOCALAPPDATA") ?: ""
            val adb = "$localAppData\\Android\\Sdk\\platform-tools\\adb.exe"
            val proc = ProcessBuilder(adb, "reverse", "tcp:8080", "tcp:80").start()
            proc.waitFor()
            println("[SmartCart] ADB reverse tcp:8080 tcp:80 executed successfully.")
        } catch (_: Exception) {
            try {
                ProcessBuilder("adb", "reverse", "tcp:8080", "tcp:80").start().waitFor()
            } catch (_: Exception) {}
        }
    }
}

tasks.matching { it.name.startsWith("install") || it.name.startsWith("assemble") }.configureEach {
    finalizedBy("adbReverse")
}