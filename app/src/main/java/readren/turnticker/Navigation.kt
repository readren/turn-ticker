package readren.turnticker

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.DisplaySettings
import androidx.compose.material.icons.twotone.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
					imageVector = Icons.TwoTone.Group,
					contentDescription = "prelude"
				)
			},
			label = {
				Text("prelude")
			},
			selected = true,
			onClick = { appViewModel.screenId = ScreenId.PRELUDE }
		)
		NavigationBarItem(
			icon = {
				Icon(
					imageVector = Icons.TwoTone.DisplaySettings,
					contentDescription = "view mode"
				)
			},
			label = {
				Text("view mode")
			},
			selected = true,
			onClick = { appViewModel.screenId = ScreenId.VIEW_MODE }
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
			onClick = { appViewModel.screenId = ScreenId.TIMERS }
		)
	}
}