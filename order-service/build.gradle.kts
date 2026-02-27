plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    api(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    // Kafka (kapt 처리를 위해 명시적으로 추가)
    implementation("org.springframework.kafka:spring-kafka")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    // Spring Retry
    implementation("org.springframework.retry:spring-retry:2.0.5")
    implementation("org.springframework:spring-aspects")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testRuntimeOnly("com.h2database:h2")
}