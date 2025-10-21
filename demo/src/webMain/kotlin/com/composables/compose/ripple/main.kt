@file:OptIn(ExperimentalWasmJsInterop::class)

package com.composables.compose.ripple

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlin.js.Promise

external interface Navigator {
    val clipboard: Clipboard
}

external interface Clipboard {
    fun writeText(text: String): Promise<JsAny>
}

external val navigator: Navigator

actual suspend fun copyToClipboard(text: String) {
    navigator.clipboard.writeText(text)
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    println("Start main")
    ComposeViewport {
        App()
    }
}