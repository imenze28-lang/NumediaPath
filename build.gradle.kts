// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Dans le build.gradle situé à la RACINE du projet
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}