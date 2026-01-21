package com.bignerdranch.codapizza.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.bignerdranch.codapizza.core.model.Pizza
import com.bignerdranch.codapizza.core.model.Topping
import com.bignerdranch.codapizza.core.model.ToppingPlacement
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class PizzaBuilderViewModel(private val orderingRepository: OrderingRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(
        PizzaBuilderUiState(
            pizza = Pizza(),
            isLoadingToppings = false,
            toppings = listOf(),
            priceState = PriceState.Unknown,
            isOrdering = false
        )
    )

    val uiState: StateFlow<PizzaBuilderUiState>
        get() = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>(replay = 0)

    val navEvent: Flow<String>
        get() = _navEvent.asSharedFlow()

    init {
        fetchToppings()
        updatePrice(uiState.value.pizza)
    }

    private fun fetchToppings() {
        _uiState.update { it.copy(isLoadingToppings = true) }

        viewModelScope.launch {
            val toppings = orderingRepository.getToppings()

            _uiState.update {
                it.copy(isLoadingToppings = false, toppings = toppings)
            }
        }
    }

    private fun updatePrice(pizza: Pizza) {
        _uiState.update { it.copy(priceState = PriceState.Unknown) }

        viewModelScope.launch {
            try {
                val formattedPrice = orderingRepository.calculateFormattedPrice(pizza)
                _uiState.update { it.copy(priceState = PriceState.Calculated(formattedPrice)) }
            } catch (ex: Exception) {
                _uiState.update { it.copy(priceState = PriceState.Error) }
            }
        }
    }

    fun addTopping(topping: Topping, placement: ToppingPlacement?) {
        _uiState.update { oldState ->
            val newPizza = oldState.pizza.withTopping(topping, placement)
            updatePrice(newPizza)
            oldState.copy(pizza = newPizza)
        }
    }

    private var placeOrderJob: Job? = null

    fun placeOrder() {
        _uiState.update { it.copy(isOrdering = true) }

        placeOrderJob = viewModelScope.launch {
            val justPlacedOrderId = orderingRepository.placeOrder(uiState.value.pizza)
            _navEvent.emit(justPlacedOrderId)
            _uiState.update { it.copy(isOrdering = false) }
        }
    }

    fun cancelingOrder() {
        placeOrderJob?.cancel()
        placeOrderJob = null
        _uiState.update { it.copy(isOrdering = false) }
    }
}

data class PizzaBuilderUiState(
    val pizza: Pizza,
    val isLoadingToppings: Boolean,
    val toppings: List<Topping>,
    val priceState: PriceState,
    val isOrdering: Boolean
)

sealed class PriceState {
    data object Unknown : PriceState()
    class Calculated(val price: String) : PriceState()
    data object Error : PriceState()
}

class PizzaBuilderViewModelFactory(
    private val orderingRepository: OrderingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return PizzaBuilderViewModel(orderingRepository) as T
    }
}
