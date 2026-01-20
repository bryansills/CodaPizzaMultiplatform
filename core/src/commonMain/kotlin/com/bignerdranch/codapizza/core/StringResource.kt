package com.bignerdranch.codapizza.core

import androidx.compose.runtime.Composable

enum class StringResource {
    AppName,
    PlaceOrderButton,
    OrderPlacedToast,
    PlacementPrompt,
    PlacementNone,
    PlacementLeft,
    PlacementRight,
    PlacementAll,
    ToppingBasil,
    ToppingMushroom,
    ToppingOlive,
    ToppingPeppers,
    ToppingPepperoni,
    ToppingPineapple,
    PizzaPreview,
    StatusNotStarted,
    StatusAccepted,
    StatusBeingPrepared,
    StatusBeingDelivered,
    StatusDelivered,
    CurrentOrderStatus,
}

@Composable
expect fun getStringResource(stringResource: StringResource): String

@Composable
expect fun getStringResource(stringResource: StringResource, vararg formatArgs: Any): String