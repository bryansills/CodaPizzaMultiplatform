package com.bignerdranch.codapizza.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.bignerdranch.codapizza.core.ui.AppTheme
import com.bignerdranch.codapizza.core.ui.PizzaBuilder
import com.bignerdranch.codapizza.core.ui.PizzaBuilderScreen
import com.bignerdranch.codapizza.core.ui.PizzaTracker
import com.bignerdranch.codapizza.core.ui.PizzaTrackerScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(PizzaBuilder::class, PizzaBuilder.serializer())
            subclass(PizzaTracker::class, PizzaTracker.serializer())
        }
    }
}

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val orderingRepository = remember(scope) {
        OrderingRepository(coroutineScope = scope)
    }

    CompositionLocalProvider(LocalOrderingRepository provides orderingRepository) {
        AppTheme {
            val backStack = rememberNavBackStack(config, PizzaBuilder)

            NavDisplay(
                backStack = backStack,
                onBack = backStack::removeLastOrNull,
                entryProvider = entryProvider {
                    entry<PizzaBuilder> {
                        PizzaBuilderScreen(
                            onOrder = { backStack.add(PizzaTracker(orderId = it)) }
                        )
                    }
                    entry<PizzaTracker> { PizzaTrackerScreen(it.orderId) }
                }
            )
        }
    }
}