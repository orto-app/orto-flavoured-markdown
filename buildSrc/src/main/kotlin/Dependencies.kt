object OFM {
    private const val baseVersion = "0.1.1"
    const val snapshot = true
    const val group = "garden.orto"
    var version = if (snapshot) {
        baseVersion.substringBefore("-").split('.').let { (major, minor, patch) ->
            "$major.$minor.${patch.toInt() + 1}-SNAPSHOT"
        }
    } else {
        baseVersion
    }
}

object Versions {
    // Gradle plugins
    const val android = "7.4.2"
    const val binaryCompatibilityValidator = "0.13.1"
    const val kotlin = "1.8.20"

    // Dependencies
    const val dokka = "1.8.10"
    const val markdown = "0.4.1"

    // Android
    const val minSdk = 24
    const val compileSdk = 33
    const val targetSdk = 33
}

object Deps {
    object Gradle {
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val android = "com.android.tools.build:gradle:${Versions.android}"
        const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}"
        const val compiler_embeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:${Versions.kotlin}"
    }

    object Kotlin {
        const val common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
        const val jvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val js = "org.jetbrains.kotlin:kotlin-stdlib-js:${Versions.kotlin}"
    }

    object Test {
        const val common = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
        const val jvm = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
        const val js = "org.jetbrains.kotlin:kotlin-test-js:${Versions.kotlin}"
    }

    object Jetbrains {
        const val markdown = "org.jetbrains:markdown:${Versions.markdown}"
    }
}