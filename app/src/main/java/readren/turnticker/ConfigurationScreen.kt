package readren.turnticker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun ConfigurationScreen() {
	val appViewModel: AppViewModel = viewModel()
	var viewModeMenuExpanded: Boolean by remember { mutableStateOf(false) }
	Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth()) {
		Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
			Text("What is shown by timers?", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 28.sp)
			Box(Modifier.fillMaxWidth(), Alignment.Center) {
				Row(
					Modifier
						.border(width = 2.dp, color = MaterialTheme.colorScheme.outline)
						.background(MaterialTheme.colorScheme.surfaceDim)
				) {
					Text(
						text = appViewModel.viewMode.displayName,
						fontSize = 20.sp,
						color = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier
							.clickable { viewModeMenuExpanded = true }
							.padding(8.dp),
					)
					Icon(
						Icons.TwoTone.ArrowDropDown,
						null,
						Modifier
							.align(Alignment.CenterVertically)
							.rotate(if (viewModeMenuExpanded) 180f else 0f),
						MaterialTheme.colorScheme.onSurface
					)
				}
				DropdownMenu(
					expanded = viewModeMenuExpanded,
					onDismissRequest = { viewModeMenuExpanded = false }
				) {
					ViewMode.entries.forEach { viewMode ->
						DropdownMenuItem(
							text = { Text(viewMode.displayName) },
							onClick = {
								appViewModel.viewMode = viewMode
								viewModeMenuExpanded = false
							}
						)
					}
				}
			}


			when (appViewModel.viewMode) {
				ViewMode.REMAINING_ABSOLUTE -> {
					TimerInput("Initial time", appViewModel.initialRemainingTime) { timerValue, unit ->
						appViewModel.initialRemainingTime = timerValue * unit.millis
					}
					TimerInput("Bonus per round", appViewModel.remainingTimeBonusPerRound) { timerValue, unit ->
						appViewModel.remainingTimeBonusPerRound = timerValue * unit.millis
					}
				}

				ViewMode.REMAINING_RELATIVE -> {
					TimerInput("Time difference threshold", appViewModel.initialTimeDifferenceThreshold) { timerValue, unit ->
						appViewModel.initialTimeDifferenceThreshold = timerValue * unit.millis
					}
					TimerInput("Bonus per round", appViewModel.thresholdBonusPerRound) { timerValue, unit ->
						appViewModel.thresholdBonusPerRound = timerValue * unit.millis
					}
				}

				ViewMode.CONSUMED_ABSOLUTE -> {}
				ViewMode.CONSUMED_RELATIVE -> {}
			}
		}
	}
}

