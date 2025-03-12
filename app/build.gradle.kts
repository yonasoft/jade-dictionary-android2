plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.yonasoft.jadedictionary"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yonasoft.jadedictionary"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    implementation(libs.tiny.pinyin)
    implementation(libs.tiny.pinyin.lexicons)
    implementation(libs.pinyin4j)

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
