import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version "0.39.0"
    kotlin("jvm") version("1.5.30")
    kotlin("plugin.serialization") version "1.5.30"
    // id("kkorg.jetbrains.kotlin.plugin.serialization") version "1.5.30"
    `java-library`

}

group="com.begemot"
version="1.0"

repositories {
    mavenLocal(){
        metadataSources {
            mavenPom()
           // artifact()
            ignoreGradleMetadataRedirection()
        }
    }
    mavenCentral()
    //jcenter()  //without it jsoup wont load!!
}


dependencies {
    implementation(platform("com.begemot.knewsplatform-bom:deps:0.0.1"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-serialization-jvm")
    implementation("io.ktor:ktor-client-encoding")
    implementation("io.ktor:ktor-client-logging-jvm")
    //implementation("io.ktor:ktor-client-android")
    //implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-cio")

    implementation("org.jsoup:jsoup")
    implementation("ch.qos.logback:logback-classic")
    implementation("io.github.microutils:kotlin-logging-jvm")


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
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    //freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
    jvmTarget = "1.8"
    //freeCompilerArgs = listOf("-Xinline-classes")
}