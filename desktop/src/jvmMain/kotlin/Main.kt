import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bignerdranch.codapizza.core.ui.AppTheme
import com.bignerdranch.codapizza.core.ui.PizzaBuilderScreen

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AppTheme {
            PizzaBuilderScreen()
        }
    }
}