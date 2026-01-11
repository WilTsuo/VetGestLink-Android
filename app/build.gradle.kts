plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "pt.ipleiria.estg.dei.vetgestlink"
    compileSdk {
        version = release(34)
    }

    defaultConfig {
        applicationId = "pt.ipleiria.estg.dei.vetgestlink"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
// ===== CORE BÁSICO (SEM KOTLIN) =====
// Usamos 'core' em vez de 'core-ktx' para evitar dependências Kotlin
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

// ===== MATERIAL DESIGN =====
    implementation("com.google.android.material:material:1.10.0")

// ===== LAYOUT =====
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

// ===== FRAGMENT BÁSICO (SEM KOTLIN) =====
// Usamos 'fragment' em vez de 'fragment-ktx' para manter simplicidade
    implementation("androidx.fragment:fragment:1.6.2")

// ===== CARDVIEW =====
// Para criar cards programaticamente (sem RecyclerView)
    implementation("androidx.cardview:cardview:1.0.0")

// ===== VOLLEY (HTTP Client) =====
    implementation("com.android.volley:volley:1.2.1")

//===== GLIDE (LOADER DE IMAGENS) =====
    implementation("com.github.bumptech.glide:glide:4.15.1")

// ===== SWIPE REFRESH =====
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

// ===== TESTES =====
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}