plugins {
	java
	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.4")
	implementation("org.springframework.boot:spring-boot-starter-security:3.0.4")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.0.4")
	implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
	implementation("org.modelmapper:modelmapper:3.1.1")
	implementation("com.auth0:java-jwt:4.3.0")
	compileOnly("org.projectlombok:lombok:1.18.26")
	runtimeOnly("org.postgresql:postgresql:42.5.4")
	annotationProcessor("org.projectlombok:lombok:1.18.26")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
	testImplementation("org.springframework.security:spring-security-test:6.0.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
