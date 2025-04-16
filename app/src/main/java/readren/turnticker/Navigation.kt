package readren.turnticker

import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Timer
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun Navigation(modifier: Modifier = Modifier, appViewModel: AppViewModel = viewModel()) {

	NavigationBar(
		containerColor = MaterialTheme.colorScheme.surfaceVariant,
		modifier = modifier
	) {
		NavigationBarItem(
			icon = {
				Icon(
					imageVector = Icons.TwoTone.Settings,
					contentDescription = "configuration"
				)
			},
			label = {
				Text("configuration")
			},
			selected = true,
			onClick = { appViewModel.stage = Stage.CONFIGURATION }
		)
		NavigationBarItem(
			icon = {
				Icon(
					imageVector = Icons.TwoTone.Timer,
					contentDescription = "timers"
				)
			},
			label = {
				Text("timers")
			},
			selected = false,
			onClick = { appViewModel.stage = Stage.MATCH }
		)
	}
}