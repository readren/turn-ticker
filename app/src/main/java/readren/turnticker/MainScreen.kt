package readren.turnticker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun MainScreen(scaffoldPaddings: PaddingValues = PaddingValues(), viewModel: AppViewModel = viewModel()): Unit {
	when (viewModel.stage) {
		Stage.CONFIGURATION -> ConfigurationScreen(scaffoldPaddings)

		Stage.MATCH -> MatchScreen(scaffoldPaddings)
	}
}