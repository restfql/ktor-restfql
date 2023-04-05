group = "org.example"
version = "1.0"

val ktor_version = "2.+"
val gql_version = "16.+"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    `maven-publish`
    `java-library`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation("com.graphql-java:graphql-java:$gql_version")
    compileOnly("io.ktor:ktor-server-content-negotiation:$ktor_version")
    compileOnly("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/restfql/ktor-restfql")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}