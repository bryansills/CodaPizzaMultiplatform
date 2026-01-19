package com.bignerdranch.codapizza.core

import androidx.compose.runtime.Composable
import com.bignerdranch.codapizza.core.ui.AppTheme
import com.bignerdranch.codapizza.core.ui.PizzaBuilderScreen

@Composable
fun App() {
    AppTheme {
        PizzaBuilderScreen()
    }
}