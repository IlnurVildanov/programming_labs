//plugins {
//    id("java")
//}
//
//group = "org.example"
//version = "1.0-SNAPSHOT"
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation("org.postgresql:postgresql:42.6.0")
//}
//
//
//tasks.jar {
//    manifest {
//        attributes["Main-Class"] = "org.example.ServerMain"
//    }
//}
//
//java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(17))
//    }
//}
//
//tasks.withType<JavaCompile> {
//    options.encoding = "UTF-8"
//}
//
//tasks.withType<JavaExec> {
//    systemProperty("file.encoding", "UTF-8")
//}

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1" // Плагин для создания jar с зависимостями
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.6.0") // PostgreSQL-драйвер
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Указание версии Java
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" // Установка кодировки
}

tasks.withType<JavaExec> {
    systemProperty("file.encoding", "UTF-8") // Установка системной кодировки
}

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to "org.example.ServerMain" // Указание главного класса
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Конфигурация shadowJar для создания "fat JAR" с зависимостями
tasks {
    shadowJar {
        archiveBaseName.set("Server")
        archiveClassifier.set("")
        archiveVersion.set("1.0-SNAPSHOT")
        mergeServiceFiles() // Для корректного объединения файлов ресурсов
        manifest {
            attributes(
                    "Main-Class" to "org.example.ServerMain" // Указание главного класса
            )
        }
    }
}

tasks.build {
    dependsOn(tasks.shadowJar) // Добавление shadowJar в цепочку сборки
}
