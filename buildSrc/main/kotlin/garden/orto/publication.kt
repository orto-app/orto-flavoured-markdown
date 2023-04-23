package garden.orto

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.PublicationChannel.MavenCentral
import org.jetbrains.PublicationChannel.MavenCentralSnapshot
import java.net.URI

fun Project.registerPublicationFromKotlinPlugin(publicationName: String, artifactId: String) {
    configure<PublishingExtension> {
        publications {
            (findByName(publicationName) as? MavenPublication)?.apply {
                this.artifactId = artifactId

                artifact(tasks["javadocJar"])
                configurePom()
            }
        }
    }
}

private fun MavenPublication.configurePom() {
    pom {
        name.set("markdown")
        description.set("Markdown parser in Kotlin")
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        url.set("https://github.com/JetBrains/markdown")
        scm {
            url.set("https://github.com/JetBrains/markdown")
            connection.set("scm:git:git://github.com/JetBrains/markdown.git")
        }
        developers {
            developer {
                id.set("valich")
                name.set("Valentin Fondaratov")
                email.set("fondarat@gmail.com")
                organization.set("JetBrains")
                organizationUrl.set("https://jetbrains.com")
            }
        }
    }
}
