package com.bignerdranch.codapizza.core

import androidx.compose.runtime.Composable

@Composable
actual fun getStringResource(stringResource: StringResource): String {
    return "TBD".format()
}

@Composable
actual fun getStringResource(stringResource: StringResource, vararg formatArgs: Any): String {
    return "TBD"
}