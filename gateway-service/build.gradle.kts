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
    // Remove or keep commented out:
    // implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Add the reactive web starter instead:
    //The error occurs because Spring Cloud Gateway is built on top of Spring 
    //WebFlux (reactive stack) and cannot work with Spring MVC (servlet stack) in the same application. 
    //They use different threading models and processing approaches.
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
} 