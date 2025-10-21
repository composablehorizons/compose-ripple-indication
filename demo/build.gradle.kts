@file:OptIn(ExperimentalDistributionDsl::class, ExperimentalWasmDsl::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.hotreload)
}

kotlin {
    jvm()

    wasmJs {
        outputModuleName = "composeApp"

        browser {
            distribution {
                val rootDirPath = project.rootDir.path
                outputDirectory = File("$rootDirPath/dist/${project.name}")
            }
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }

        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.composables.composeunstyled)
            implementation(project(":ripple-indication"))
            implementation("com.composables:icons-lucide:1.0.0")
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude("org.jetbrains.compose.material")
                exclude("org.jetbrains.compose.material3")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.composables.compose.material.ripple.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.composables.compose.material.ripple"
            packageVersion = "1.0.0"
        }
    }
}

compose.experimental {
    web.application {}
}