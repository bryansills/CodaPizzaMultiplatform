package com.bignerdranch.codapizza.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource as androidStringResource

@Composable
actual fun getStringResource(stringResource: StringResource, vararg formatArgs: Any): String {
    return androidStringResource(stringResource.resourceId, formatArgs)
}

private val StringResource.resourceId: Int
    get() {
        return when (this) {
            else -> R.string.app_name
        }
    }