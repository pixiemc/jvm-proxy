plugins {
    id("java")
}

group = "rip.pixie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("com.google.guava:guava:33.4.0-jre")

}
