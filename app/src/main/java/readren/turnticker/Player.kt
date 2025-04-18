package readren.turnticker

typealias DurationMillis = Long
typealias Instant = Long

val MILLIS_IN_A_SECOND: Long = 1000

class Player(val name: String) {
	var timeConsumedInPreviousRounds: DurationMillis = 0
		private set

	private var timeConsumedPreviouslyInCurrentRound: DurationMillis = 0
	private var timeSpentInPreviousRound: DurationMillis = 0

	private var resumeInstant: Instant? = null

	fun isConsuming(): Boolean = resumeInstant != null

	fun allowsToFinishRound(): Boolean = resumeInstant == null && timeConsumedPreviouslyInCurrentRound > 0L

	fun allowsToUndoRound(): Boolean = timeConsumedPreviouslyInCurrentRound == 0L

	fun pause(currentInstant: Instant) {
		resumeInstant?.let { ri ->
			this.timeConsumedPreviouslyInCurrentRound += currentInstant - ri
			resumeInstant = null
		}
	}

	fun resume(currentInstant: Instant) {
		if (resumeInstant == null) resumeInstant = currentInstant
	}

	fun undoLastResume() {
		resumeInstant = null
	}

	fun onNewRound(currentInstant: Instant) {
		pause(currentInstant)
		timeSpentInPreviousRound = timeConsumedPreviouslyInCurrentRound
		timeConsumedInPreviousRounds += timeConsumedPreviouslyInCurrentRound
		timeConsumedPreviouslyInCurrentRound = 0
	}

	fun undoLastRound() {
		timeConsumedInPreviousRounds -= timeSpentInPreviousRound
		timeConsumedPreviouslyInCurrentRound = timeSpentInPreviousRound
		timeSpentInPreviousRound = 0
	}

	fun calcTimeConsumedAt(currentInstant: Instant): DurationMillis {
		val timeConsumedSinceLastResume = resumeInstant?.let { currentInstant - it } ?: 0
		return timeConsumedInPreviousRounds + timeConsumedPreviouslyInCurrentRound + timeConsumedSinceLastResume
	}
}

