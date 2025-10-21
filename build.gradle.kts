import org.jetbrains.compose.internal.utils.getLocalProperty

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrains.kotlin.multiplatform) apply false
    alias(libs.plugins.jetbrains.compose.hotreload) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.jetbrains.compose.compiler) apply false
    alias(libs.plugins.android.library) apply false
    id("io.github.gradle-nexus.publish-plugin").version("2.0.0-rc-1")
}

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(
                getLocalProperty("sonatype.stagingProfileId") ?: System.getenv("SONATYPE_STAGING_PROFILE_ID")
            )
            username.set(getLocalProperty("sonatype.username") ?: System.getenv("OSSRH_USERNAME"))
            password.set(getLocalProperty("sonatype.password") ?: System.getenv("OSSRH_PASSWORD"))
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}