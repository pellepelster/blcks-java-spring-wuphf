plugins {
    java
    // the standard gradle packaging plugin: `distZip` bundles the start scripts, the
    // application jar and its runtime dependencies into one archive
    application
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "de.solidblocks.examples"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    // the visit log lives in postgres when the service is linked to one; liquibase creates
    // the table it is kept in, so the application owns its schema rather than assuming one
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")
    // the masking of secrets and the two representations of a visit are unit tested; the
    // blcks integration tests that deploy this example need a machine and take minutes
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
    mainClass = "de.solidblocks.examples.javaspringwuphf.ExampleApplication"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// The deliverable is always `java-spring-wuphf.zip`, whatever the project is versioned.
tasks.named<Zip>("distZip") {
    archiveFileName = "java-spring-wuphf.zip"
}

// Same idea for the standalone jar: always `java-spring-wuphf.jar`.
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName = "java-spring-wuphf.jar"
}

// `dist` is the one task to remember; `build` still produces the same archive.
tasks.register("dist") {
    group = "distribution"
    description = "Builds the distribution archive at build/distributions/java-spring-wuphf.zip."
    dependsOn(tasks.named("distZip"))
}
