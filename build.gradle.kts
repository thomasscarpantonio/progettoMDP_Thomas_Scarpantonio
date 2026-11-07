plugins {
    java
    application
    id ("application")
    id ("org.openjfx.javafxplugin") version ("0.1.0")
}

group = "it.unicam.cs.mpgc.rpg129518"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

javafx {
    version = "21.0.11"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("it.unicam.cs.mpgc.rpg129518.gui.Launcher")
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=javafx.graphics",
        "--enable-native-access=ALL-UNNAMED",
        "--sun-misc-unsafe-memory-access=allow"
    )
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}