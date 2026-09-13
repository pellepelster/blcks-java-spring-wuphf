plugins {
    // resolves the java toolchain below, so the build does not depend on the JDK that happens
    // to be on PATH
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "java-spring-wuphf"
