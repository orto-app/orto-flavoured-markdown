fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
    signing
}

apply(from = rootProject.file("./gradle/publish.gradle.kts"))

group = OFM.group
version = OFM.version

kotlin {
    jvmToolchain(8)
    android {
        publishAllLibraryVariants()
    }
    jvm()
    js(IR) {
        nodejs {}
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }

    // darwin
    if (ideaActive.not()) {
        // intel
        macosX64()
        ios()

        // apple silicon
        macosArm64()
        iosSimulatorArm64()
    } else {
        if (isAppleSilicon) {
            // apple silicon
            macosArm64()
            iosSimulatorArm64()
        } else {
            // intel
            macosX64()
            iosX64()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
                implementation(Deps.Kotlin.common)
                implementation(Deps.Jetbrains.markdown)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Deps.Test.common)
            }
        }
        val androidMain by getting {
            dependencies {
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(Deps.Test.jvm)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Deps.Kotlin.jvm)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(Deps.Test.jvm)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Deps.Kotlin.js)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(Deps.Test.js)
            }
        }
        val darwinMain by creating {
            dependsOn(commonMain)
        }
        val darwinTest by creating {
            dependsOn(commonTest)
        }

        // darwin
        if (ideaActive.not()) {
            // intel
            val macosX64Main by getting {
                dependsOn(darwinMain)
            }
            val macosX64Test by getting {
                dependsOn(darwinTest)
            }
            val iosMain by getting {
                dependsOn(darwinMain)
            }
            val iosTest by getting {
                dependsOn(darwinTest)
            }
            // apple silicon
            val macosArm64Main by getting {
                dependsOn(darwinMain)
            }
            val macosArm64Test by getting {
                dependsOn(darwinTest)
            }
            val iosSimulatorArm64Main by getting {
                dependsOn(darwinMain)
            }
            val iosSimulatorArm64Test by getting {
                dependsOn(darwinTest)
            }
        } else {
            if (isAppleSilicon) {
                // apple silicon
                val macosArm64Main by getting {
                    dependsOn(darwinMain)
                }
                val macosArm64Test by getting {
                    dependsOn(darwinTest)
                }
                val iosSimulatorArm64Main by getting {
                    dependsOn(darwinMain)
                }
                val iosSimulatorArm64Test by getting {
                    dependsOn(darwinTest)
                }
            } else {
                // intel
                val macosX64Main by getting {
                    dependsOn(darwinMain)
                }
                val macosX64Test by getting {
                    dependsOn(darwinTest)
                }
                val iosX64Main by getting {
                    dependsOn(darwinMain)
                }
                val iosX64Test by getting {
                    dependsOn(darwinTest)
                }
            }
        }
    }
}

android {
    namespace = "garden.orto.ofm"
    compileSdk = Versions.compileSdk
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }
}
