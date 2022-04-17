plugins {
	id("java")
}

group = "org.springframework"
version = "5.1.14.BUILD-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

	compile(project(":child-spring-boot"))
//	optional("org.springframework.data:spring-data-jdbc")
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}