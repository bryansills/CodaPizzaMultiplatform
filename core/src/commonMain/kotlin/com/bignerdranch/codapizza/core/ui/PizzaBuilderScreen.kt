package com.bignerdranch.codapizza.core.ui

//import android.widget.Toast
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.tooling.preview.Preview
//import com.bignerdranch.codapizza.core.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.bignerdranch.codapizza.core.LocalOrderingRepository
import com.bignerdranch.codapizza.core.OrderingRepository
import com.bignerdranch.codapizza.core.PizzaBuilderUiState
import com.bignerdranch.codapizza.core.PizzaBuilderViewModel
import com.bignerdranch.codapizza.core.PizzaBuilderViewModelFactory
import com.bignerdranch.codapizza.core.PriceState
import com.bignerdranch.codapizza.core.StringResource
import com.bignerdranch.codapizza.core.getStringResource
import com.bignerdranch.codapizza.core.model.Pizza
import com.bignerdranch.codapizza.core.model.Topping
import com.bignerdranch.codapizza.core.model.ToppingPlacement
import kotlinx.serialization.Serializable

@Serializable
data object PizzaBuilder : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PizzaBuilderScreen(
    onOrder: (String) -> Unit,
    modifier: Modifier = Modifier,
    orderingRepository: OrderingRepository = LocalOrderingRepository.current,
) {
    val viewModel: PizzaBuilderViewModel = viewModel(
        factory = PizzaBuilderViewModelFactory(orderingRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.navEvent.collect {
            onOrder(it)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(getStringResource(StringResource.AppName)) }
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Column {
                    ToppingsList(
                        pizza = uiState.pizza,
                        toppings = uiState.toppings,
                        isLoadingTopping = uiState.isLoadingToppings,
                        onToppingSelected = viewModel::addTopping,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = true)
                    )

                    OrderButton(
                        formattedPrice = uiState.formattedPrice,
                        enabled = uiState.priceState is PriceState.Calculated,
                        onClick = viewModel::placeOrder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }

                if (uiState.isOrdering) {
                    val bgColor = MaterialTheme.colorScheme.background
                        .copy(alpha = 0.75f)
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(bgColor)
                            .fillMaxSize(),
                    ) {
                        Text(
                            getStringResource(StringResource.OrderingPizza),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Button(
                            onClick = viewModel::cancelingOrder
                        ) {
                            Text(getStringResource(StringResource.Cancel))
                        }
                    }
                }
            }
        }
    )
}

private val PizzaBuilderUiState.formattedPrice: String
    @Composable get() {
        return when (this.priceState) {
            is PriceState.Calculated -> this.priceState.price
            PriceState.Error -> getStringResource(StringResource.ErrorPrice)
            PriceState.Unknown -> getStringResource(StringResource.UnknownPrice)
        }
    }

@Composable
private fun ToppingsList(
    pizza: Pizza,
    toppings: List<Topping>,
    isLoadingTopping: Boolean,
    onToppingSelected: (Topping, ToppingPlacement?) -> Unit,
    modifier: Modifier = Modifier
) {
    var toppingBeingAdded by rememberSaveable { mutableStateOf<Topping?>(null) }

    toppingBeingAdded?.let { topping ->
        ToppingPlacementDialog(
            topping = topping,
            onSetToppingPlacement = { placement ->
                onToppingSelected(topping, placement)
            },
            onDismissRequest = {
                toppingBeingAdded = null
            }
        )
    }

    LazyColumn(
        modifier = modifier
    ) {
        item {
            PizzaHeroImage(
                pizza = pizza,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (isLoadingTopping) {
            item {
                Text(
                    text = "Hey we are loading the toppings",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            items(toppings) { topping ->
                ToppingCell(
                    topping = topping,
                    placement = pizza.toppings[topping],
                    onClickTopping = {
                        toppingBeingAdded = topping
                    }
                )
            }
        }
    }
}

@Composable
private fun OrderButton(
    formattedPrice: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled
    ) {
        Text(
            text = getStringResource(StringResource.PlaceOrderButton, formattedPrice)
        )
    }
}
