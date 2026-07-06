import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    alias(libs.plugins.google.gms.google.services)
//    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.techuntried.accountsbasics2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.techuntried.accountsbasics2"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField("String", "API_TOKEN", "\"${properties.getProperty("API_TOKEN")}\"")
        buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding {
            enable = true
        }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    //foreground
    implementation(libs.androidx.lifecycle.process)
    //ads
    implementation(libs.ads.mobile.sdk)
    //review
    implementation(libs.review)
    implementation(libs.review.ktx)
    //custom tab intent
    implementation(libs.androidx.browser)
    //constraint layout
    implementation(libs.androidx.constraintlayout.compose)
    //coil
    implementation(libs.coil.compose)
    //firebase
//    implementation(libs.firebase.messaging)
//    implementation(libs.firebase.crashlytics)
//    implementation(libs.firebase.analytics)
    //room
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    //data store
    implementation(libs.androidx.datastore.preferences)
    //navigation
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    //ktor client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)
    //hilt
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    //splash screen
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}