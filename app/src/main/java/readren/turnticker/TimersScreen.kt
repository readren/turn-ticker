package readren.turnticker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

private enum class FitState {
	UNKNOWN, FIT, DO_NOT_FIT
}

@Preview
@Composable
fun TimersScreen(appViewModel: AppViewModel = viewModel()) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Section {
			Row(
				Modifier
					.align(Alignment.CenterHorizontally)
					.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text("Round ${appViewModel.finishedRounds + 1}", style = MaterialTheme.typography.headlineLarge)
				Button(onClick = { appViewModel.finishRound() }, enabled = appViewModel.isFinishRoundEnabled) {
					Text("Finish round")
				}
				IconButton(onClick = { appViewModel.undoRound() }, enabled = appViewModel.isUndoRoundEnabled) {
					Icon(Icons.AutoMirrored.TwoTone.Undo, null, modifier = Modifier.size(200.dp))
				}
			}
		}
		Section {
			Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
				Text("Participant", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(horizontal = 4.dp))
				Text(appViewModel.viewMode.header, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 4.dp))
			}
			var fitState: FitState by remember { mutableStateOf(FitState.UNKNOWN) }

			if (fitState == FitState.UNKNOWN || fitState == FitState.FIT) {
				Layout(
					appViewModel.getPlayers().map { player ->
						@Composable {
							if (player == appViewModel.selectedPlayer) {
								SelectedPlayer()
							} else {
								UnselectedPlayer(player.name)
							}
						}
					},
				) { measurables, constraints ->
					val playersMeasurables = measurables.map { it.first() }
					val availableHeight = constraints.maxHeight
					val minIntrinsicsHeights = playersMeasurables.map { it.minIntrinsicHeight(constraints.maxWidth) }
					val totalMinIntrinsicHeight = minIntrinsicsHeights.sum()
					fitState = if (availableHeight >= totalMinIntrinsicHeight) FitState.FIT else FitState.DO_NOT_FIT
					val extraHeightPerPlayer = (constraints.maxHeight - totalMinIntrinsicHeight) / playersMeasurables.size
					val playersPlaceables = minIntrinsicsHeights.zip(playersMeasurables) { minIntrinsicHeight, playerMeasurable ->
						val height = if (extraHeightPerPlayer > 0) minIntrinsicHeight + extraHeightPerPlayer else minIntrinsicHeight
						playerMeasurable.measure(constraints.copy(minHeight = height, maxHeight = height))
					}
					layout(constraints.maxWidth, constraints.maxHeight) {
						var y = 0
						playersPlaceables.forEach {
							it.placeRelative(0, y)
							y += it.height
						}
					}
				}
			} else {
				Column(
					Modifier
						.verticalScroll(rememberScrollState())
						.fillMaxHeight()
				) {
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
	}
}


@Composable
fun SelectedPlayer() {
	val appViewModel: AppViewModel = viewModel()
	appViewModel.selectedPlayer?.let { player ->
		val timePresented = appViewModel.timePresentedFor(player)
		Surface(color = MaterialTheme.colorScheme.primary, border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline), modifier = Modifier.clip(MaterialTheme.shapes.medium)) {
			Column(verticalArrangement = Arrangement.SpaceAround) {
				Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
					Text(
						player.name, maxLines = 1, overflow = TextOverflow.Clip, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier
							.padding(start = 8.dp, end = 8.dp)
							.weight(1f)
					)
					TimerDisplay(timePresented)
				}
				Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
					IconButton(onClick = { appViewModel.resumeSelectedPlayer() }, enabled = !player.isConsuming()) {
						Icon(Icons.TwoTone.PlayArrow, null, modifier = Modifier.size(200.dp))
					}
					IconButton(onClick = { appViewModel.pauseSelectedPlayer() }, enabled = player.isConsuming()) {
						Icon(Icons.TwoTone.Pause, null, modifier = Modifier.size(200.dp))
					}
					IconButton(onClick = { appViewModel.undoLastResume() }, enabled = player.isConsuming()) {
						Icon(Icons.AutoMirrored.TwoTone.Undo, null, modifier = Modifier.size(200.dp))
					}
				}
			}
		}
	}
}

@Composable
fun UnselectedPlayer(playerName: String) {
	val appViewModel: AppViewModel = viewModel()
	appViewModel.getPlayer(playerName)?.let { player ->
		val timePresented = appViewModel.timePresentedFor(player)
		val allowsToFinishRound = player.allowsToFinishRound()
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.clip(MaterialTheme.shapes.medium)
				.border(BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant))
				.background(if (allowsToFinishRound) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer)
				.fillMaxWidth()
				.clickable { appViewModel.changeSelectedPlayer(player) },
		) {
			Text(
				playerName, maxLines = 1, overflow = TextOverflow.Clip, style = MaterialTheme.typography.displaySmall, modifier = Modifier
					.padding(start = 8.dp, end = 8.dp)
					.weight(1f)
			)
			TimerDisplay(timePresented)
		}
	}
}

@Composable
fun TimerDisplay(timeInMillis: DurationMillis) {
	val absTime = if (timeInMillis < 0) -timeInMillis else timeInMillis
	val seconds = (absTime / 1000) % 60
	val minutes = (absTime / (1000 * 60)) % 60
	val hours = (absTime / (1000 * 60 * 60))
	val formattedTime = String.format(Locale.getDefault(), "${if (timeInMillis < 0) "-" else " "}%02d:%02d:%02d", hours, minutes, seconds)

	Text(
		text = formattedTime,
		style = MaterialTheme.typography.displayMedium,
		modifier = Modifier.padding(8.dp),
	)
}