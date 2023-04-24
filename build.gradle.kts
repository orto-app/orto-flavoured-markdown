plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.library").version(Versions.android).apply(false)
    kotlin("android").version(Versions.kotlin).apply(false)
    kotlin("multiplatform").version(Versions.kotlin).apply(false)
}

buildscript {
    dependencies {
        classpath(Deps.Gradle.kotlin)
    }
}