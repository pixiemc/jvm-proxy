plugins {
    application
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "rip.pixie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    shadow(implementation("org.java-websocket:Java-WebSocket:1.5.2")!!)
    shadow(implementation("com.google.guava:guava:33.4.0-jre")!!)

}

application {
    mainClass.set("rip.pixie.jvmproxy.Main")
}
