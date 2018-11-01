

plugins {
    application
    kotlin("jvm") version "1.3.0"
}


repositories {
    jcenter()
}


application {
    mainClassName = "cz.fit.metacentrum.MainKt"
}

dependencies {
    compile(kotlin("stdlib"))
    testCompile("junit:junit:4.11")
    testCompile(kotlin("org.jetbrains.kotlin:kotlin-test-junit"))
}
