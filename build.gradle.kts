plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.spring") version "2.4.20"
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

extra["kotlin.version"] = "2.4.20"

extra["testcontainers.version"] = "1.21.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-assertions-core:6.2.5")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

application {
    mainClass = "de.solidblocks.examples.javaspringwuphf.ExampleApplicationKt"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Zip>("distZip") {
    archiveFileName = "blcks-java-spring-wuphf.zip"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName = "blcks-java-spring-wuphf.jar"
}

tasks.register("dist") {
    group = "distribution"
    description = "Builds the executable jar at build/libs/blcks-java-spring-wuphf.jar and the distribution archive at build/distributions/blcks-java-spring-wuphf.zip."
    dependsOn(tasks.named("bootJar"), tasks.named("distZip"))
}
