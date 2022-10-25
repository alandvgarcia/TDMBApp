buildscript {
    val compose_version by extra("1.0.1")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        with(Deps.Classpath){
            classpath(kotlin)
            classpath(androidTools)
            classpath(sqlDelight)
            classpath(serialization)
            classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.13.3")
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}