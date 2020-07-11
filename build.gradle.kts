import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm") version("1.3.72")
}

group="com.begemot"
//version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation (kotlin("stdlib-jdk8"))
    //testCompile group: 'junit', name: 'junit', version: '4.12'
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}