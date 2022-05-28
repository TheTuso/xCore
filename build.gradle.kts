plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.3.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

group = "pl.tuso.core"
version = "1.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    implementation("io.lettuce:lettuce-core:6.1.8.RELEASE")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.0")
}

tasks {
    shadowJar {
        relocate("io.lettuce.core", "pl.tuso.lib.lettuce")
        relocate("com.github.benmanes.caffeine", "pl.tuso.lib.caffeine")

        archiveBaseName.set("xCore")
        archiveClassifier.set("")
        archiveVersion.set(version)
    }
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/thetuso/xcore")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            groupId = "pl.tuso.core"
            artifactId = "xcore"
            version = "1.0"
            from(components["java"])
        }
    }
}