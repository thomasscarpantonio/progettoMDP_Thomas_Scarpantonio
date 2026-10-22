
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

plugins {
    id ("application")
    id ("org.openjfx.javafxplugin") version ("0.1.0")
}

javafx {
    version = "21.0.11"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    // Sostituisci con il percorso completo della classe che farà partire il gioco
    mainClass.set("it.unicam.cs.mpgc.rpg129518.prova")
}