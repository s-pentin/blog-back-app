plugins {
    id("java")
    id("war")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val springVersion = "6.2.1"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
dependencies {
    implementation("org.springframework:spring-webmvc:$springVersion")
    implementation("org.springframework:spring-context:$springVersion")
    implementation("org.springframework:spring-jdbc:$springVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    testImplementation("org.testcontainers:testcontainers:1.20.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")

    testImplementation("jakarta.servlet:jakarta.servlet-api:6.1.0")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework:spring-test:$springVersion")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.jayway.jsonpath:json-path:2.9.0")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.war {
    archiveBaseName.set("ROOT")
    archiveVersion.set("")
}

val tomcatHome: String = project.findProperty("tomcat.home") as String?
    ?: System.getenv("TOMCAT_HOME")
    ?: error("Set tomcat.home in gradle.properties or TOMCAT_HOME environment variable")

tasks.register("deploy") {
    dependsOn("war")
    group = "deployment"
    description = "Build WAR and copy to Tomcat webapps"
    doLast {
        val warFile = layout.buildDirectory.file("libs/ROOT.war").get().asFile
        val webapps = file("${tomcatHome}/webapps")
        if (!webapps.exists()) {
            error("Tomcat webapps directory not found: ${webapps.absolutePath}")
        }
        copy {
            from(warFile)
            into(webapps)
        }
        println("Deployed ${warFile.name} to ${webapps.absolutePath}")
    }
}

tasks.register("undeploy") {
    group = "deployment"
    description = "Remove WAR and extracted app from Tomcat webapps"
    doLast {
        val webapps = file("${tomcatHome}/webapps")
        delete(file("${webapps}/blog-backend.war"))
        delete(file("${webapps}/blog-backend"))
        println("Undeployed blog-backend from ${webapps.absolutePath}")
    }
}

val isWindows = System.getProperty("os.name").lowercase().contains("win")
val scriptExt = if (isWindows) ".bat" else ".sh"

tasks.register<Exec>("tomcatStop") {
    group = "tomcat"
    description = "Stop Tomcat server"

    workingDir("$tomcatHome/bin")
    commandLine("./catalina$scriptExt", "stop")

    doLast {
        println("⛔ Tomcat is stopping...")
        Thread.sleep(1500)   // небольшая пауза после остановки
    }
}

tasks.register<Exec>("tomcatStart") {
    group = "tomcat"
    description = "Start Tomcat server"

    workingDir("$tomcatHome/bin")
    commandLine("./catalina$scriptExt", "start")

    doLast {
        println("✅ Tomcat is starting...")
        Thread.sleep(3500)   // Tomcat 11 запускается дольше
        println("✅ Tomcat should be ready")
    }
}

tasks.register("restart") {
    group = "deployment"
    description = "Полный цикл: тесты → остановить → удалить → задеплоить → запустить"
    dependsOn("test", "tomcatStop", "undeploy", "deploy", "tomcatStart")}

tasks.named("undeploy") {
    mustRunAfter("tomcatStop")
}

tasks.named("deploy") {
    mustRunAfter("undeploy")
}

tasks.named("tomcatStart") {
    mustRunAfter("deploy")
}

tasks.test {
    useJUnitPlatform()
}