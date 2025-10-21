package com.composables.compose.ripple

import androidx.compose.ui.window.singleWindowApplication
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual suspend fun copyToClipboard(text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(StringSelection(text), null)
}

fun main() = singleWindowApplication(title = "Compose Material Ripple Playground") {
    App()
}