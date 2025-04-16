package readren.turnticker

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun ConfigurationScreen(scaffoldPaddings: PaddingValues = PaddingValues(), appViewModel: AppViewModel = viewModel()) {
	Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(scaffoldPaddings)) {
		Column {
			Column(modifier = Modifier.border(width = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)) {
				Text(text = "Time", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 30.sp)
				TimerInput("Initial time") { timerValue, unit ->
					appViewModel.initialTime = timerValue * unit.millis
				}
				TimerInput("Round bonus time") { timerValue, unit ->
					appViewModel.roundBonus = timerValue * unit.millis
				}
			}
			Column(modifier = Modifier.border(width = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)) {
				Text(text = "Players", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 30.sp)
				NameEntry(
					names = appViewModel.getPlayers().map { it.name },
					isNameValid = { name -> appViewModel.isValidName(name) },
					onNameAdded = { name -> appViewModel.addPlayer(name) },
					onNameRemoved = { name -> appViewModel.removePlayer(name) },
					addButtonText = "Add",
					placeholder = "enter a player name",
				)
			}
		}

	}
}

