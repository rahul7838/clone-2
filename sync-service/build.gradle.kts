plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.quora"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Debezium dependencies with Jersey exclusions
    implementation("io.debezium:debezium-api:2.5.0.Final")
    implementation("io.debezium:debezium-embedded:2.5.0.Final") {
        exclude(group = "org.glassfish.jersey.containers")
        exclude(group = "org.glassfish.jersey.core")
        exclude(group = "org.glassfish.jersey.inject")
        exclude(group = "org.glassfish.jersey.ext")
    }
    implementation("io.debezium:debezium-connector-mongodb:2.5.0.Final")
    
    // Kafka for event streaming
    implementation("org.springframework.kafka:spring-kafka")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
} 