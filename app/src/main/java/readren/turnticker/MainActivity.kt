package readren.turnticker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import readren.turnticker.ui.theme.TurnTickerTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		setContent { Content() }
	}
}

@Preview
@Composable
fun Content() {
	TurnTickerTheme {
		Scaffold(
			bottomBar = { Navigation() }
		) { padding ->
			Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(padding)) {
				when (viewModel<AppViewModel>().stage) {
					Stage.CONFIGURATION -> ConfigurationScreen()
					Stage.MATCH -> MatchScreen()
				}
			}
		}
	}
}