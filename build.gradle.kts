//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version "0.28.0"
    `java-library`
    kotlin("jvm") version("1.3.72")
    kotlin("plugin.serialization") version("1.3.72")

}

group="com.begemot"
//version '1.0-SNAPSHOT'

repositories {
    mavenLocal(){
        metadataSources {
            mavenPom()
           // artifact()
            ignoreGradleMetadataRedirection()
        }
    }
    mavenCentral()

}


dependencies {
    implementation(platform("com.begemot.knewsplatform-bom:deps:0.0.1"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.jetbrains.kotlinx:kotlinx-serialization-runtime")
    //implementation (kotlin("stdlib-jdk8"))
    //implementation(Versions.JSOUP)
    //implementation("io.ktor:ktor-client-serialization-jvm:1.3.2")
   //implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0") ????
    //testCompile group: 'junit', name: 'junit', version: '4.12'

}

/*
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}*/