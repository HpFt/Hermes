plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.10")
    id("org.springframework.boot").version("2.1.1.RELEASE")
    id("io.spring.dependency-management").version("1.0.6.RELEASE")
}

repositories {
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")

    implementation("org.webjars:jquery:3.3.1-1")
    implementation("org.webjars:bootstrap:4.2.1")

    implementation("commons-io:commons-io:2.6")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
}