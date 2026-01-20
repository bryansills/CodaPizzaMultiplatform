package com.bignerdranch.codapizza.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation3.runtime.NavKey
import com.bignerdranch.codapizza.core.LocalOrderingRepository
import com.bignerdranch.codapizza.core.OrderStatus
import com.bignerdranch.codapizza.core.OrderingRepository
import com.bignerdranch.codapizza.core.StringResource
import com.bignerdranch.codapizza.core.getStringResource
import kotlinx.serialization.Serializable

@Serializable
data class PizzaTracker(val orderId: String) : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaTrackerScreen(
    orderId: String,
    modifier: Modifier = Modifier,
    orderingRepository: OrderingRepository = LocalOrderingRepository.current,
) {
    val orderStatus by orderingRepository.getOrderStatus(orderId)
        .collectAsState(OrderStatus.NotStarted)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(getStringResource(StringResource.AppName)) }
            )
        },
        content = { padding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                Text(
                    getStringResource(StringResource.CurrentOrderStatus),
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
//                OrderProgressBar(
//                    orderStatus = orderStatus,
//                    modifier = Modifier.fillMaxWidth().height(72.dp).padding(16.dp)
//                )
                Text(
                    getStringResource(orderStatus.stringResource),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    )
}
