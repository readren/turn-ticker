package readren.turnticker

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Stage { CONFIGURATION, MATCH }

class AppViewModel : ViewModel() {
	var stage: Stage by mutableStateOf<Stage>(Stage.CONFIGURATION)

	var initialTime: DurationMillis by mutableStateOf(0)
	var roundBonus: DurationMillis by mutableStateOf(0)
	private val players = mutableStateListOf<Player>()

	var selectedPlayer: Player? by mutableStateOf(null)
		private set

	fun getPlayers(): List<Player> = players

	fun isValidName(name: String): Boolean {
		return players.none { it.name == name }
	}

	fun addPlayer(name: String): Unit {
		if (isValidName(name)) players.add(Player(name))
	}

	fun removePlayer(name: String): Unit {
		players.removeIf { it.name == name }
	}

	fun changeSelectedPlayer(player: Player?) {
		selectedPlayer = player
		if (player != null) startScheduledUpdates()
	}

	private fun startScheduledUpdates() {
		viewModelScope.launch {
			while (true) {
				val delayMs = selectedPlayer?.timeUntilNextVisibleChange(SystemClock.elapsedRealtime()) ?: break
				if (delayMs > 0L) delay(delayMs)
				selectedPlayer = selectedPlayer // force recompose
			}
		}
	}

	fun reset(): Unit {
		players.forEach { it.reset() }
	}
}