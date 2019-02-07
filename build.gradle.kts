import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val appName = "clusterize"

repositories {
    mavenCentral()
}

buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.3.10"
    var junitPlatformVersion: String by extra
    junitPlatformVersion = "1.0.0"

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.junit.platform:junit-platform-gradle-plugin:$junitPlatformVersion")
    }
}

val junitPlatformVersion: String by extra
val kotlinVersion: String by extra
val junitVersion = "5.0.0"
val jacksonVersion = "2.9.7"
val guiceVersion = "4.2.2"
val mockitoVersion = "2.23.4"

apply(plugin = "kotlin")
apply(plugin = "org.junit.platform.gradle.plugin")
apply(from = "junit5.gradle")

plugins {
    application
}

dependencies {
    val implementation by configurations
    val compile by configurations
    val testCompile by configurations
    val testRuntime by configurations

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    compile(group = "org.slf4j", name = "slf4j-api", version = "1.7.25")
    compile(group = "org.apache.logging.log4j", name = "log4j-slf4j-impl", version = "2.11.1")
    compile(group = "org.apache.logging.log4j", name = "log4j-api", version = "2.11.1")
    compile(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.11.1")

    compile("io.github.microutils:kotlin-logging:1.6.10")

    compile(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    compile(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
    compile(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion)
    compile(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = jacksonVersion)

    compile(group = "com.google.inject", name = "guice", version = guiceVersion)
    compile(group = "com.google.inject.extensions", name = "guice-multibindings", version = guiceVersion)
    compile(group = "com.github.spullara.mustache.java", name = "compiler", version = "0.9.5")

    compile(group = "org.jsoup", name = "jsoup", version = "1.11.3")

    testCompile("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testCompile("org.assertj:assertj-core:3.11.1")
    testCompile("com.google.jimfs:jimfs:1.1")
    testCompile(group = "org.mockito", name = "mockito-junit-jupiter", version = mockitoVersion)
    testCompile(group = "org.mockito", name = "mockito-core", version = mockitoVersion)

    testRuntime("org.junit.platform:junit-platform-console:$junitPlatformVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    // tests are failing in IDEA without this
    testCompile(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.3.2")

}



application {
    mainClassName = "cz.fit.metacentrum.MainKt"
    applicationName = appName
}
distributions {
    main {
        baseName = appName
    }
}


val compileKotlin by tasks.getting(KotlinCompile::class) {
    // Customise the "compileKotlin" task.
    kotlinOptions.jvmTarget = "1.8"
    doLast { println("Finished compiling Kotlin source code") }
}
val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    // Customise the "compileTestKotlin" task.
    kotlinOptions.jvmTarget = "1.8"
    doLast { println("Finished compiling Kotlin source code for testing") }
}