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

enum class ScreenId { PRELUDE, VIEW_MODE, TIMERS }
enum class ViewMode(val displayName: String, val header: String, val showsRelativeTime: Boolean) {
	CONSUMED_ABSOLUTE("absolute consumed time", "consumed time", false),
	CONSUMED_RELATIVE("relative consumed time", "consumed time", true),
	REMAINING_ABSOLUTE("absolute remaining time", "remaining time", false),
	REMAINING_RELATIVE("relative remaining time", "remaining time",true),
}

class AppViewModel : ViewModel() {
	var screenId: ScreenId by mutableStateOf(ScreenId.PRELUDE)
	var viewMode: ViewMode by mutableStateOf(ViewMode.REMAINING_ABSOLUTE)

	var initialRemainingTime: DurationMillis by mutableStateOf(60_000)
	var remainingTimeBonusPerRound: DurationMillis by mutableStateOf(0)
	var initialTimeDifferenceThreshold: DurationMillis by mutableStateOf(60_000)
	var thresholdBonusPerRound: DurationMillis by mutableStateOf(0)

	var finishedRounds: Int by mutableStateOf(0)

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

	fun finishRound() {
		finishedRounds++
		selectedPlayer = null
		players.forEach { it.onNewRound(updateAndGetCurrentTime()) }
	}

	fun reset() {
		finishedRounds = 0
		selectedPlayer = null
		players.forEachIndexed { index, player ->
			player.reset()
			players[index] = player
		}
	}

	fun timePresentedFor(targetPlayer: Player): DurationMillis {
		val timeConsumedByTargetPlayer = targetPlayer.timeConsumedAt(currentTime)
		val timeConsumedInPreviousRoundsByFasterPlayer: DurationMillis = players.minOf{ it.timeConsumedInPreviousRounds }
		when (viewMode) {
			ViewMode.REMAINING_ABSOLUTE -> return initialRemainingTime + finishedRounds * remainingTimeBonusPerRound - timeConsumedByTargetPlayer
			ViewMode.REMAINING_RELATIVE -> return initialTimeDifferenceThreshold + finishedRounds * thresholdBonusPerRound + timeConsumedInPreviousRoundsByFasterPlayer - timeConsumedByTargetPlayer
			ViewMode.CONSUMED_ABSOLUTE -> return timeConsumedByTargetPlayer
			ViewMode.CONSUMED_RELATIVE -> return timeConsumedByTargetPlayer - timeConsumedInPreviousRoundsByFasterPlayer
		}
	}

	private fun startScheduledUpdates() {
		viewModelScope.launch {
			selectedPlayer?.let {
				val timeConsumedInPreviousRoundsByFasterPlayer: DurationMillis = players.minOf{ it.timeConsumedInPreviousRounds }
				while (it.isConsuming()) {
					val timeConsumed = it.timeConsumedAt(updateAndGetCurrentTime())
					val measuredTimeConsumed =
						if (viewMode.showsRelativeTime) timeConsumed - timeConsumedInPreviousRoundsByFasterPlayer
						else timeConsumed
					val timeUntilNextVisibleChange = MILLIS_IN_A_SECOND - (measuredTimeConsumed % MILLIS_IN_A_SECOND)
					delay(timeUntilNextVisibleChange)
				}
			}
		}
	}

	private fun updateAndGetCurrentTime(): Instant {
		currentTime  = SystemClock.elapsedRealtime()
		return currentTime
	}
}