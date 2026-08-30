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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
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
    
    companion object {
//        val ORDERING_REPOSITORY_KEY = CreationExtras.Key<OrderingRepository>()
//
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val orderingRepository = this[ORDERING_REPOSITORY_KEY] as OrderingRepository
//                PizzaBuilderViewModel(
//                    orderingRepository = orderingRepository,
//                    savedStateHandle = this.createSavedStateHandle()
//                )
//            }
//        }
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
        return PizzaBuilderViewModel(orderingRepository, extras.createSavedStateHandle()) as T
    }
}
