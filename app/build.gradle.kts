plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	kotlin("plugin.serialization") version "1.5.0"
}

android {
	namespace = "com.virtuix.lyricstats"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.virtuix.lyricstats"
		minSdk = 29
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.4.3"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

val composeVersion = "1.6.7"
val coroutinesVersion = "1.8.0"
dependencies {

	implementation("androidx.core:core-ktx:1.13.1")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
	implementation("androidx.activity:activity-compose:1.9.0")
	implementation(platform("androidx.compose:compose-bom:2023.03.00"))
	implementation("androidx.compose.ui:ui:$composeVersion")
	implementation("androidx.compose.ui:ui-graphics:$composeVersion")
	implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
	implementation("androidx.compose.material3:material3")
	implementation("com.squareup.retrofit2:retrofit:2.9.0")
	implementation("com.squareup.retrofit2:converter-gson:2.9.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")

	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	androidTestImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
	androidTestImplementation("org.mockito:mockito-android:5.12.0")
	testImplementation("org.mockito:mockito-inline:5.2.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
	testImplementation("org.robolectric:robolectric:4.10")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
}