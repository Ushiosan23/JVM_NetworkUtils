plugins {
	id("java-library")
	id("org.jetbrains.dokka") version "1.4.10"
	id("maven-publish")
	kotlin("jvm") version "1.4.10"
	kotlin("plugin.serialization") version "1.4.10"
}

// Base configuration
group = "com.github.ushiosan23.networkutils"
version = "0.0.1"

// Repositories
repositories {
	mavenCentral()
	jcenter()
}

// Library Dependencies
dependencies {
	/* kotlin */
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))
	/* serialization */
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
	/* coroutines */
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0-M1")
	/* test */
	implementation("junit", "junit", "4.12")
}


// Maven configuration
publishing {
	/* repositories */
	repositories {
		/* maven publishing */
		maven {
			name = "GithubPackages"
			url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
			version = project.version as String
			// Authentication
			credentials {
				username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
				password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
			}
		}
	}
}
