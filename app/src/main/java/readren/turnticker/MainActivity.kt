package readren.turnticker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import readren.turnticker.ui.theme.TurnTickerTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		setContent {
			TurnTickerTheme {
				Scaffold(
					bottomBar = { Navigation() }
				) { padding ->
					MainScreen(padding)
				}
			}
		}
	}
}

@Preview
@Composable
fun MainPreview() {
	TurnTickerTheme {
		Scaffold(
			bottomBar = { Navigation() }
		) { padding ->
			MainScreen(padding)
		}
	}
}