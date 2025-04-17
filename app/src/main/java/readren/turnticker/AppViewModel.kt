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

	var currentTime: Instant by mutableStateOf(0)
		private set

	fun getPlayer(name: String): Player? {
		return players.find { it.name == name }
	}

	fun getPlayers(): List<Player> = players

	fun isValidName(name: String): Boolean {
		return players.none { it.name == name }
	}

	fun addPlayer(name: String) {
		if (isValidName(name)) players.add(Player(name))
	}

	fun removePlayer(name: String) {
		players.removeIf { it.name == name }
	}

	fun changeSelectedPlayer(player: Player?) {
		selectedPlayer = player
		player?.let { if (it.isConsuming()) startScheduledUpdates() }
	}

	fun pauseSelectedPlayer() {
		selectedPlayer?.pause(updateAndGetCurrentTime())
	}

	fun resumeSelectedPlayer() {
		selectedPlayer?.let {
			it.resume(updateAndGetCurrentTime())
			startScheduledUpdates()
		}
	}

	fun undoLastResume() {
		selectedPlayer?.undoLastResume()
		val sp = selectedPlayer
		selectedPlayer = null
		selectedPlayer = sp
	}

	fun reset() {
		players.forEachIndexed { index, player ->
			player.reset()
			players[index] = player
		}
	}

	private fun startScheduledUpdates() {
		viewModelScope.launch {
			selectedPlayer?.let {
				while (it.isConsuming()) {
					delay(it.timeUntilNextVisibleChange(updateAndGetCurrentTime()))
				}
			}
		}
	}

	private fun updateAndGetCurrentTime(): Instant {
		currentTime  = SystemClock.elapsedRealtime()
		return currentTime
	}
}