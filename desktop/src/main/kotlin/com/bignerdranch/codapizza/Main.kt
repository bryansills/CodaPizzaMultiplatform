package com.bignerdranch.codapizza

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bignerdranch.codapizza.core.App
import com.bignerdranch.codapizza.core.StringResource
import com.bignerdranch.codapizza.core.getStringResource

fun main() = application {
    Window(
        title = getStringResource(StringResource.AppName),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}