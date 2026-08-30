package com.bignerdranch.codapizza.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.serialization.saved
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import com.bignerdranch.codapizza.core.model.Pizza
import com.bignerdranch.codapizza.core.model.Topping
import com.bignerdranch.codapizza.core.model.ToppingPlacement
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds

class PizzaBuilderViewModel(
    private val orderingRepository: OrderingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pizzaState: MutableState<Pizza> by savedStateHandle.saved(MutableStateSerializer()) {
        mutableStateOf(Pizza())
    }
    var pizza: Pizza by pizzaState
        private set

    private val availableToppingsState: MutableState<List<Topping>> by savedStateHandle.saved(MutableStateSerializer()) {
        mutableStateOf(listOf())
    }
    var availableToppings by availableToppingsState
        private set

    val price: StateFlow<PriceState> = snapshotFlow { pizza }
        .transformLatest { currentPizza ->
            emit(PriceState.Unknown)

            val newState = try {
                val formattedPrice = orderingRepository.calculateFormattedPrice(currentPizza)
                PriceState.Calculated(formattedPrice)
            } catch (ex: Exception) {
                currentCoroutineContext().ensureActive()
                PriceState.Error
            }
            emit(newState)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = PriceState.Unknown
        )

    private val isLoadingToppingsState: MutableState<Boolean> by savedStateHandle.saved(MutableStateSerializer()) {
        mutableStateOf(true)
    }
    var isLoadingToppings by isLoadingToppingsState
        private set

    private val isOrderingState: MutableState<Boolean> by savedStateHandle.saved(MutableStateSerializer()) {
        mutableStateOf(false)
    }
    var isOrdering by isOrderingState
        private set

    init {
        viewModelScope.launch {
            try {
                availableToppings = orderingRepository.getToppings()
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                // TODO: add a failure state
            } finally {
                isLoadingToppings = false
            }
        }
    }

    private val _navEvent = MutableSharedFlow<String>(replay = 0)

    val navEvent: Flow<String>
        get() = _navEvent.asSharedFlow()

    fun addTopping(topping: Topping, placement: ToppingPlacement?) {
        pizza = pizza.withTopping(topping, placement)
    }

    private var placeOrderJob: Job? = null

    fun placeOrder() {
        isOrdering = true

        placeOrderJob = viewModelScope.launch {
            try {
                val justPlacedOrderId = orderingRepository.placeOrder(pizza)
                _navEvent.emit(justPlacedOrderId)
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                // TODO: have a failure state
            } finally {
                isOrdering = false
            }
        }
    }

    fun cancelingOrder() {
        placeOrderJob?.cancel()
        placeOrderJob = null
        isOrdering = false
    }
}

sealed class PriceState {
    data object Unknown : PriceState()
    class Calculated(val price: String) : PriceState()
    data object Error : PriceState()
}

class PizzaBuilderViewModelFactory(
    private val orderingRepository: OrderingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return PizzaBuilderViewModel(orderingRepository, extras.createSavedStateHandle()) as T
    }
}
