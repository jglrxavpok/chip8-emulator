import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.internal.os.OperatingSystem

group = "org.jglr"
version = "1.0-SNAPSHOT"

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.0"

    repositories {
        mavenCentral()
    }
    
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }
    
}

apply {
    plugin("java")
    plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}

dependencies {
    val lwjglVersion = "3.1.2"
    val lwjglNatives = when(OperatingSystem.current()) {
        OperatingSystem.WINDOWS -> "natives-windows"
        OperatingSystem.LINUX -> "natives-linux"
        OperatingSystem.MAC_OS -> "natives-macos"
        else -> "unknownos"
    }
    compile("com.github.kotlin-graphics:kotlin-unsigned:v2.1")
    compile("org.lwjgl:lwjgl:${lwjglVersion}")
    compile("org.lwjgl:lwjgl-glfw:${lwjglVersion}")
    compile("org.lwjgl:lwjgl-opengl:${lwjglVersion}")

    runtime("org.lwjgl:lwjgl:${lwjglVersion}:${lwjglNatives}")
    runtime("org.lwjgl:lwjgl-glfw:${lwjglVersion}:${lwjglNatives}")
    runtime("org.lwjgl:lwjgl-opengl:${lwjglVersion}:${lwjglNatives}")

    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

