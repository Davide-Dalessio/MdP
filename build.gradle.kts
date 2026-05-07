plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

application {
    mainClass = "it.unicam.cs.mpgc.rpg125556.MdPApplication"
}



group = "it.unicam.cs.mpgc"
version = "0.0.1-SNAPSHOT"
description = "MdP"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

javafx {
    version = "25"
    modules("javafx.controls", "javafx.fxml")
}


tasks.withType<Test> {
    useJUnitPlatform()
}
