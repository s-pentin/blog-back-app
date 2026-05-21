plugins {
    id("java")
    id("war")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.springframework:spring-webmvc:6.1.14")
    implementation("org.springframework:spring-context:6.1.14")
    implementation("org.springframework.data:spring-data-jdbc:4.0.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    implementation("org.postgresql:postgresql:42.7.4")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}