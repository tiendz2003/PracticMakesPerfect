// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    id ("androidx.navigation.safeargs.kotlin") version "2.9.3" apply false
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
}