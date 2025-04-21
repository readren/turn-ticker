package readren.turnticker

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
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
fun PreludeScreen() {
	val appViewModel: AppViewModel = viewModel()
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Section {
			Text(text = "Preparation", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 30.sp)
			Button(onClick = { appViewModel.reset() }) {
				Text("Reset timers")
			}
		}
		Section {
			Text(text = "Participants", fontSize = 30.sp)
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