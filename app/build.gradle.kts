import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.remon.mdmdeviceowner"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.remon.mdmdeviceowner"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs{
        create("release"){
            storeFile = file("../keystore/keystore.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.enterprise.feedback)

}

// Task to generate SHA-256 for release APK
        tasks.register("generateReleaseApkSha256") {
            group = "distribution"
            description = "Generate SHA-256 checksum for signed release APK"

            doLast {
                val apkDir = file("$buildDir/outputs/apk/release")
                val apkFile = apkDir.listFiles()?.find { it.name.endsWith(".apk") }

                if (apkFile == null) {
                    throw GradleException("❌ Release APK not found. Build release first.")
                }

                val digest = MessageDigest.getInstance("SHA-256")
                apkFile.inputStream().use { input ->
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        digest.update(buffer, 0, read)
                    }
                }

                val sha256 = digest.digest()
                    .joinToString("") { String.format("%02x", it) }

                val outFile = File(apkDir, "${apkFile.name}.sha256")
                outFile.writeText(sha256)

                println("✅ SHA-256 generated: $sha256")
                println("File saved at: ${outFile.absolutePath}")
            }
        }

// Automatically run SHA-256 task after assembleRelease
tasks.whenTaskAdded {
    if (name == "assembleRelease") {
        finalizedBy("generateReleaseApkSha256")
    }
}