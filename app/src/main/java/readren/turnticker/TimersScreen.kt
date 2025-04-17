package readren.turnticker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Undo
import androidx.compose.material.icons.twotone.Pause
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

@Preview
@Composable
fun TimersScreen(appViewModel: AppViewModel = viewModel()) {
	Surface {
		Column {
			Row(Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
				Text("Round ${appViewModel.finishedRounds + 1}", style = MaterialTheme.typography.headlineLarge)
				Spacer(Modifier.width(8.dp))
				Button(onClick = { appViewModel.finishRound() }) {
					Text("Finish round")
				}
			}
			Spacer(modifier = Modifier.width(8.dp))
			Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
				Text("Participant", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(4.dp))
				Text(appViewModel.viewMode.header, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(4.dp))
			}
			Spacer(modifier = Modifier.width(8.dp))
			appViewModel.getPlayers().forEach { player ->
				key(player.name) {
					if (player == appViewModel.selectedPlayer) {
						SelectedPlayer()
					} else {
						UnselectedPlayer(player.name)
					}
				}
			}
		}
	}
}


@Preview
@Composable
fun SelectedPlayer() {
	val appViewModel: AppViewModel = viewModel()
	appViewModel.selectedPlayer?.let { player ->
		val timePresented = appViewModel.timePresentedFor(player)
		Surface {
			Column {
				Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
					Text(player.name, style = MaterialTheme.typography.displaySmall)
					TimerDisplay(timePresented)
				}
				Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
					IconButton(onClick = { appViewModel.resumeSelectedPlayer() }, enabled = !player.isConsuming()) {
						Icon(Icons.TwoTone.PlayArrow, null)
					}
					IconButton(onClick = { appViewModel.pauseSelectedPlayer() }, enabled = player.isConsuming()) {
						Icon(Icons.TwoTone.Pause, null)
					}
					IconButton(onClick = { appViewModel.undoLastResume() }, enabled = player.isConsuming()) {
						Icon(Icons.AutoMirrored.TwoTone.Undo, null)
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun UnselectedPlayer(playerName: String = "Fulano") {
	val appViewModel: AppViewModel = viewModel()
	appViewModel.getPlayer(playerName)?.let { player ->
		val timePresented = appViewModel.timePresentedFor(player)
		Surface {
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth().clickable { appViewModel.changeSelectedPlayer(player) },
			) {
				Text(playerName, style = MaterialTheme.typography.displaySmall)
				Spacer(modifier = Modifier.width(8.dp))
				TimerDisplay(timePresented)
			}
		}
	}
}

@Composable
fun TimerDisplay(timeInMillis: DurationMillis) {
	val seconds = (timeInMillis / 1000) % 60
	val minutes = (timeInMillis / (1000 * 60)) % 60
	val hours = (timeInMillis / (1000 * 60 * 60))
	val formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

	Text(
		text = formattedTime,
		style = MaterialTheme.typography.displayMedium,
		modifier = Modifier.padding(8.dp)
	)
}