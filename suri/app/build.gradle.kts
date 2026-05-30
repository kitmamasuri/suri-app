import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}

// Decode debug.keystore if it is missing but the base64 representation exists
// Running this during configuration phase with correct relative/absolute rootDir
val keystoreFile = File(rootDir, "debug.keystore")
val base64File = File(rootDir, "debug.keystore.base64")
if (!keystoreFile.exists() && base64File.exists()) {
    try {
        val base64Text = base64File.readText().trim()
        val decodedBytes = Base64.getDecoder().decode(base64Text)
        keystoreFile.writeBytes(decodedBytes)
        println("SURI_BUILD: Successfully decoded debug.keystore at ${keystoreFile.absolutePath}")
    } catch (e: Exception) {
        println("SURI_BUILD_ERROR: Error decoding keystore at ${keystoreFile.absolutePath}: ${e.message}")
    }
}

android {
  namespace = "com.example"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.aistudio.periodpal.pzxwrl"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

abstract class CopyApkTask : DefaultTask() {
    @get:InputFile
    abstract val srcFile: RegularFileProperty

    @get:OutputFile
    abstract val dstFile: RegularFileProperty

    @TaskAction
    fun perform() {
        val src = srcFile.get().asFile
        val dst = dstFile.get().asFile
        if (src.exists()) {
            src.copyTo(dst, overwrite = true)
            println("Successfully copied APK to: ${dst.absolutePath}")
            
            val dst2 = File(dst.parentFile, ".build-outputs/app-debug.apk")
            if (dst2.parentFile.exists()) {
                src.copyTo(dst2, overwrite = true)
                println("Successfully copied APK to: ${dst2.absolutePath}")
            }
            
            // Also save a beautifully named APK copy in root for easy, attractive sharing!
            val fancyApk = File(dst.parentFile, "Suri_Period_Companion.apk")
            src.copyTo(fancyApk, overwrite = true)
            println("Successfully created companion APK at: ${fancyApk.absolutePath}")
        } else {
            error("Source APK not found at: ${src.absolutePath}")
        }
    }
}

tasks.register<CopyApkTask>("copyApkToRoot") {
    dependsOn("assembleDebug")
    srcFile.set(layout.buildDirectory.file("outputs/apk/debug/app-debug.apk"))
    dstFile.set(layout.projectDirectory.file("../app-debug.apk"))
}

// Automatically copy the APK to root when building
tasks.matching { it.name == "assembleDebug" }.all {
    finalizedBy("copyApkToRoot")
}

tasks.register("decodeKeystore") {
    val kFile = File(rootDir, "debug.keystore")
    val bFile = File(rootDir, "debug.keystore.base64")
    doFirst {
        if (bFile.exists()) {
            try {
                val base64Text = bFile.readText().trim()
                val decodedBytes = Base64.getDecoder().decode(base64Text)
                kFile.writeBytes(decodedBytes)
                println("SURI_BUILD_TASK: Decoded debug.keystore in execution phase at ${kFile.absolutePath}")
            } catch (e: java.lang.Exception) {
                println("SURI_BUILD_TASK_ERROR: Failed to decode debug.keystore at ${kFile.absolutePath}: ${e.message}")
            }
        }
    }
}

// Ensure preBuild depends on our decode task
tasks.matching { it.name.startsWith("preBuild") }.all {
    dependsOn("decodeKeystore")
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
    }
}




