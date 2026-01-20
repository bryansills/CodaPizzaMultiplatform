package com.bignerdranch.codapizza.core

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.bignerdranch.codapizza.core.model.Pizza
import com.bignerdranch.codapizza.core.model.Topping
import com.bignerdranch.codapizza.core.model.ToppingPlacement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderingRepository(
    private val currencyFormatter: CurrencyFormatter = getCurrencyFormatter(),
    private val coroutineScope: CoroutineScope
) {
    private val orderStatusMap: MutableMap<String, StateFlow<OrderStatus>> = mutableMapOf()

    suspend fun getToppings(): List<Topping> {
        delay(2000)
        return Topping.entries.toList()
    }

    suspend fun getToppingPlacementSuggestion(): ToppingPlacement {
        val potentialOptions = listOf(
            ToppingPlacement.All,
            ToppingPlacement.Left,
            ToppingPlacement.Right
        )
        delay(5000)
        return potentialOptions.random()
    }

    suspend fun calculateFormattedPrice(pizza: Pizza): String {
        delay(3000)
        return currencyFormatter.format(pizza.price)
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun placeOrder(pizza: Pizza): String {
        delay(5000)

        val orderId = Uuid.random().toString()
        processOrder(orderId, pizza)

        return orderId
    }

    fun getOrderStatus(pizzaId: String): Flow<OrderStatus> {
        if (pizzaId == DemoPizzaId) {
            processOrder(DemoPizzaId, Pizza())
        }

        return orderStatusMap[pizzaId] ?: throw IllegalArgumentException("Invalid ID")
    }

    private fun processOrder(pizzaId: String, pizza: Pizza) {
        val orderFlow = MutableStateFlow(OrderStatus.NotStarted)

        coroutineScope.launch {
            delay(2000)
            orderFlow.update { OrderStatus.Accepted }
            delay(2000)
            orderFlow.update { OrderStatus.BeingPrepared }

            val cookingTime = pizza.toppings.keys.size * 2500L + 1000L
            delay(cookingTime)
            orderFlow.update { OrderStatus.BeingDelivered }

            delay(3000)
            orderFlow.update { OrderStatus.Delivered }

            delay(15_000)
            orderStatusMap.remove(pizzaId)
        }

        orderStatusMap[pizzaId] = orderFlow.asStateFlow()
    }

    companion object {
        @OptIn(ExperimentalUuidApi::class)
        val DemoPizzaId: String = Uuid.random().toString()
    }
}

enum class OrderStatus( val stringResource: StringResource) {
    NotStarted(StringResource.StatusNotStarted),
    Accepted(StringResource.StatusAccepted),
    BeingPrepared(StringResource.StatusBeingPrepared),
    BeingDelivered(StringResource.StatusBeingDelivered),
    Delivered(StringResource.StatusDelivered)
}

val LocalOrderingRepository: ProvidableCompositionLocal<OrderingRepository> = compositionLocalOf { throw RuntimeException("No OrderingRepository provided :(") }
