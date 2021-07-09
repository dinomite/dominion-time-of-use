plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    kotlin("stdlib-jdk8")
}

application {
    mainClass.set("net.dinomite.energy.dominiontou.AppKt")
}
