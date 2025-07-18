plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
}

tasks.configureEach {
    if (name.contains("compileReleaseArtProfile") || name.contains("ArtProfile")) {
        enabled = false
    }
}

android {
    namespace = "com.yonasoft.jadedictionary"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yonasoft.jadedictionary"
        minSdk = 26
        targetSdk = 36
        versionCode = 11
        versionName = "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isProfileable = false
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
        buildConfig = true
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.room.runtime)
    implementation(libs.digital.ink.recognition)
    implementation(libs.common)
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.vision.common)
    implementation(libs.androidx.espresso.core)
    implementation(libs.play.services.mlkit.text.recognition.chinese)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.material3.window.size.android)
    implementation(libs.androidx.navigation.runtime.android)
    ksp(libs.androidx.room.compiler)

    implementation(libs.compose.ui.google.fonts)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core.viewmodel)
    implementation(libs.koin.core.viewmodel.navigation)
    implementation(libs.koin.annotations)
    implementation(libs.koin.ksp)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    implementation(libs.tiny.pinyin)
    implementation(libs.tiny.pinyin.lexicons)
    implementation(libs.pinyin4j)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.datastore)

    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}

room {
    schemaDirectory("$projectDir/schemas")
}

configurations {
    create("cleanedAnnotations")
    // This excludes the annotations module for the "implementation" configuration:
    getByName("implementation").exclude(group = "org.jetbrains", module = "annotations")
}
