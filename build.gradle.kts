plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.0-Beta3"
}

group = "io.github.dingyi222666"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.github.dingyi222666:luaparser:1.0.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}