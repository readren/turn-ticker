package readren.turnticker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
	Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
		Section {
			Text("Behavior settings", fontSize = 28.sp)
			CheckboxContainer("Auto pause when unselected") {
				Checkbox(checked = appViewModel.autoPauseWhenUnselected, onCheckedChange = { appViewModel.autoPauseWhenUnselected = it })
			}
			CheckboxContainer("Auto resume when selected") {
				Checkbox(checked = appViewModel.autoResumeWhenSelected, onCheckedChange = { appViewModel.autoResumeWhenSelected = it })
			}
		}

		Section {
			Text("What is shown by timers?", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 28.sp)
			Box(contentAlignment = Alignment.Center) {
				Row(
					modifier = Modifier
						.clip(MaterialTheme.shapes.medium)
						.border(width = 2.dp, color = MaterialTheme.colorScheme.outline)
						.background(MaterialTheme.colorScheme.secondary),
				) {
					Text(
						text = appViewModel.viewMode.displayName,
						fontSize = 20.sp,
						color = MaterialTheme.colorScheme.onSecondary,
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
						MaterialTheme.colorScheme.onSecondary
					)
				}
				DropdownMenu(
					expanded = viewModeMenuExpanded,
					onDismissRequest = { viewModeMenuExpanded = false },

					) {
					ViewMode.entries.forEach { viewMode ->
						DropdownMenuItem(
							text = { Text(viewMode.displayName, fontSize = 16.sp) },
							onClick = {
								appViewModel.viewMode = viewMode
								viewModeMenuExpanded = false
							},
							modifier = Modifier.padding(8.dp),
						)
					}
				}
			}

			Text(appViewModel.viewMode.description, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))

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
					TimerInput("Base time", appViewModel.initialTimeDifferenceThreshold) { timerValue, unit ->
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


