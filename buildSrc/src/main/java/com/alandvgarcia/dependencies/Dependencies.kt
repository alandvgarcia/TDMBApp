import org.gradle.kotlin.dsl.extra

object Version{
    const val kotlin = "1.6.0"
    const val sqlDelight = "1.5.2"
    const val ktorVersion = "1.6.2-native-mm-eap-196"
    const val coroutinesShared = "1.5.1-new-mm-dev2"
    const val serialization = "1.2.2"
    const val kermit = "1.0.0"
    const val gradleAGPPlugin = "7.0.3"
}


object Deps {
    object Classpath {
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:${Version.kotlin}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
        const val androidTools = "com.android.tools.build:gradle:${Version.gradleAGPPlugin}"
        const val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Version.sqlDelight}"
    }
    object Ktor {
        const val ktorClientCore =  "io.ktor:ktor-client-core:${Version.ktorVersion}"
        const val ktorClientSerialization = "io.ktor:ktor-client-serialization:${Version.ktorVersion}"
        const val ktorClientIos = "io.ktor:ktor-client-ios:${Version.ktorVersion}"
        const val ktorClientAndroid = "io.ktor:ktor-client-android:${Version.ktorVersion}"
        const val ktorLog = "io.ktor:ktor-client-logging:${Version.ktorVersion}"
    }
    object Coroutines{
        const val coroutinesShared = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutinesShared}"
    }
    object SqlDelight{
        const val sqlDelightRuntime = "com.squareup.sqldelight:runtime:${Version.sqlDelight}"
        const val sqlDelightCoroutineExtensions = "com.squareup.sqldelight:coroutines-extensions:${Version.sqlDelight}"
        const val sqlDelightIos = "com.squareup.sqldelight:native-driver:${Version.sqlDelight}"
        const val sqlDelightAndroid= "com.squareup.sqldelight:android-driver:${Version.sqlDelight}"
    }
    object Log{
        const val kermit = "co.touchlab:kermit:${Version.kermit}"
    }
    object Kotlinx{
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.serialization}"
    }


}