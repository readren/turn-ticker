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
enum class ViewMode(val displayName: String, val header: String, val showsRelativeTime: Boolean, val description: String) {
	CONSUMED_ABSOLUTE("absolute consumed time", "consumed time", false, "Timers show the total time spent by each participant, calculated as: [Total Time Spent]."),
	CONSUMED_RELATIVE("relative consumed time", "consumed time", true, "Timers show the time spent by each participant relative to the time spent by the fastest participant until the previous round inclusive, calculated as: [Total Time Spent] – [Fastest Player’s Total Time Spent Until Previous Round]." ),
	REMAINING_ABSOLUTE("absolute remaining time", "remaining time", false, "Timers show the available time for the current turn, calculated as: [Initial Time] + [Bonus Per Round]*[Number of Rounds Finished] – [Total Time Spent] "),
	REMAINING_RELATIVE("relative remaining time", "net remaining time",true, "Timers show the available time for the current turn, calculated as: [Base Time] + [Fastest Player’s Total Time Spent Until Previous Round] + [Bonus Per Round]*[Number of Rounds Finished] – [Total Time Spent]"),
}

class AppViewModel : ViewModel() {
	var screenId: ScreenId by mutableStateOf(ScreenId.PRELUDE)
	var viewMode: ViewMode by mutableStateOf(ViewMode.REMAINING_ABSOLUTE)

	var initialRemainingTime: DurationMillis by mutableStateOf(60_000)
	var remainingTimeBonusPerRound: DurationMillis by mutableStateOf(0)
	var initialTimeDifferenceThreshold: DurationMillis by mutableStateOf(60_000)
	var thresholdBonusPerRound: DurationMillis by mutableStateOf(0)

//	var lastTouchedRound: Int by mutableStateOf(0)
	var finishedRounds: Int by mutableStateOf(0)

	private val players = mutableStateListOf<Player>()

	var selectedPlayer: Player? by mutableStateOf(null)
		private set

	var currentTime: Instant by mutableStateOf(0)
		private set

	var isFinishRoundEnabled: Boolean by mutableStateOf(false)
		private set
	var isUndoRoundEnabled: Boolean by mutableStateOf(false)
		private set

	var autoPauseWhenUnselected: Boolean by mutableStateOf(true)
	var autoResumeWhenSelected: Boolean by mutableStateOf(true)


	fun getPlayer(name: String): Player? {
		return players.find { it.name == name }
	}

	fun getPlayers(): List<Player> = players

	fun isValidName(name: String): Boolean {
		return players.none { it.name == name }
	}

	fun addPlayer(name: String) {
		if (isValidName(name)) players.add(Player(name))
		updateActionsEnablers()
	}

	fun removePlayer(name: String) {
		getPlayer(name)?.pause(currentTime) // necessary to stop the scheduled updates for this player
		players.removeIf { it.name == name }
		updateActionsEnablers()
	}

	fun changeSelectedPlayer(player: Player?) {
		if (autoPauseWhenUnselected) pauseSelectedPlayer()
		selectedPlayer = player
		player?.let {
			if (autoResumeWhenSelected && !it.allowsToFinishRound()) resumeSelectedPlayer()
			if (it.isConsuming()) startScheduledUpdates()
		}
	}

	fun pauseSelectedPlayer() {
		selectedPlayer?.pause(updateAndGetCurrentTime())
//		lastTouchedRound = finishedRounds + 1
		updateActionsEnablers()
	}

	fun resumeSelectedPlayer() {
		selectedPlayer?.let {
			it.resume(updateAndGetCurrentTime())
			startScheduledUpdates()
		}
		updateActionsEnablers()
	}

	fun undoLastResume() {
		selectedPlayer?.undoLastResume()
		val sp = selectedPlayer
		selectedPlayer = null
		selectedPlayer = sp
		updateActionsEnablers()
	}

	fun finishRound() {
		if (isFinishRoundEnabled) {
			finishedRounds++
			selectedPlayer = null
			players.forEach { it.onNewRound(updateAndGetCurrentTime()) }
			updateActionsEnablers()
		}
	}

	fun undoRound() {
		if (isUndoRoundEnabled) {
			finishedRounds--
			selectedPlayer = null
			players.forEach { it.undoLastRound() }
			updateActionsEnablers()
		}
	}

	fun reset() {
//		lastTouchedRound = 0
		finishedRounds = 0
		selectedPlayer = null
		players.forEachIndexed { index, player ->
			players[index] = Player(player.name)
		}
		updateActionsEnablers()
	}

	fun timePresentedFor(targetPlayer: Player): DurationMillis {
		val timeConsumedByTargetPlayer = targetPlayer.calcTimeConsumedAt(currentTime)
		val timeConsumedInPreviousRoundsByFasterPlayer: DurationMillis = players.minOf{ it.timeConsumedInPreviousRounds }
		when (viewMode) {
			ViewMode.REMAINING_ABSOLUTE -> return initialRemainingTime + finishedRounds * remainingTimeBonusPerRound - timeConsumedByTargetPlayer
			ViewMode.REMAINING_RELATIVE -> return initialTimeDifferenceThreshold + finishedRounds * thresholdBonusPerRound + timeConsumedInPreviousRoundsByFasterPlayer - timeConsumedByTargetPlayer
			ViewMode.CONSUMED_ABSOLUTE -> return timeConsumedByTargetPlayer
			ViewMode.CONSUMED_RELATIVE -> return timeConsumedByTargetPlayer - timeConsumedInPreviousRoundsByFasterPlayer
		}
	}

	private fun updateActionsEnablers() {
		isFinishRoundEnabled = players.all { it.allowsToFinishRound() }
		isUndoRoundEnabled = players.all { it.allowsToUndoRound() } // && finishedRounds >= lastTouchedRound
	}

	private fun startScheduledUpdates() {
		viewModelScope.launch {
			selectedPlayer?.let {
				val timeConsumedInPreviousRoundsByFasterPlayer: DurationMillis = players.minOf{ it.timeConsumedInPreviousRounds }
				while (it.isConsuming()) {
					val timeConsumed = it.calcTimeConsumedAt(updateAndGetCurrentTime())
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