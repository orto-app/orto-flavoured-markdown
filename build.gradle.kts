//import garden.orto.registerPublicationFromKotlinPlugin

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)


plugins {
    kotlin("multiplatform") version "1.8.20"
    `maven-publish`
    signing
}

group = properties("group").get()
val baseVersion = properties("version").get()
version = if (properties("snapshot").toString().toBoolean()) {
    baseVersion.substringBefore("-").split('.').let { (major, minor, patch) ->
        "$major.$minor.${patch.toInt() + 1}-SNAPSHOT"
    }
} else {
    baseVersion
}


repositories {
    mavenCentral()
}

kotlin {
    listOf(
        linuxX64("linuxX64"),
        macosX64("macosX64"),
        macosArm64("macosArm64"),
        iosX64("iosX64"),
        iosArm64("iosArm64"),
        iosSimulatorArm64("iosSimulatorArm64"),
        watchosSimulatorArm64("watchosSimulatorArm64"),
        tvosSimulatorArm64("tvosSimulatorArm64")
    ).forEach {
        it.binaries.sharedLib {
            baseName = "${it.name}Native"
        }
    }
    mingwX64 {
        binaries {
            sharedLib {
                baseName = "libnative"
            }
        }
    }
    jvm {
        jvmToolchain(11)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnit {}
        }
    }
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
    ios()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
                implementation(libs.markdown)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
        }
        val jsMain by getting
        val jsTest by getting {
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        listOf(
            "linuxX64", "mingwX64", "macosX64", "macosArm64", "iosX64", "iosArm64", "iosSimulatorArm64",
            "watchosSimulatorArm64", "tvosSimulatorArm64"
        ).forEach { target ->
            getByName("${target}Main").dependsOn(nativeMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        listOf("linuxX64", "mingwX64", "macosX64", "macosArm64").forEach { target ->
            val sourceSet = getByName("${target}Test")
            sourceSet.dependsOn(nativeTest)
        }
        val iosTest by getting {
        }
    }
}

val publicationsToArtifacts = mapOf(
    "kotlinMultiplatform" to "ofm",
    "jvm" to "ofm-jvm",
    "js" to "ofm-js",
    "linuxX64" to "ofm-linuxx64",
    "mingwX64" to "ofm-mingwx64",
    "macosX64" to "ofm-macosx64",
    "macosArm64" to "ofm-macosarm64",
    "iosX64" to "ofm-iosx64",
    "iosArm64" to "ofm-iosarm64",
    "iosSimulatorArm64" to "ofm-iossimulatorarm64",
    "watchosSimulatorArm64" to "ofm-watchossimulatorarm64",
    "tvosSimulatorArm64" to "ofm-tvossimulatorarm64",
    "metadata" to "ofm-metadata"
)

subprojects {
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/orto-app/orto-flavoured-markdown")
                credentials {
                    username = System.getenv("USERNAME")
                    password = System.getenv("TOKEN")
                }
            }
        }
        publications {
            register<MavenPublication>("gpr") {
                from(components["java"])
            }
        }
    }
}

//publicationsToArtifacts.forEach { publicationName, artifactId ->
//    registerPublicationFromKotlinPlugin(publicationName, artifactId)
//}
//signPublicationsIfNecessary(*publicationsToArtifacts.keys.toTypedArray())
//configureSonatypePublicationIfNecessary()
//configureBintrayPublicationIfNecessary()
