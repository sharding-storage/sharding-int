plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "sharding.storage"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
	testImplementation("org.hamcrest:hamcrest-all:1.3")
	testImplementation("io.rest-assured:spring-web-test-client:5.5.5")
	testImplementation("io.rest-assured:spring-mock-mvc:5.5.5")
	testImplementation("io.rest-assured:json-schema-validator:5.5.5")
	testImplementation("io.rest-assured:xml-path:5.5.5")
	testImplementation("io.rest-assured:json-path:5.5.5")
	testImplementation("io.rest-assured:rest-assured:5.5.5")
	testImplementation("io.rest-assured:kotlin-extensions:5.5.5")
	testImplementation("junit:junit:4.13.2")
	testImplementation(kotlin("test"))
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
