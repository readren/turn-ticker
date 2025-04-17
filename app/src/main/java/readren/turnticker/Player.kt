package readren.turnticker

typealias DurationMillis = Long
typealias Instant = Long

val MILLIS_IN_A_SECOND: Long = 1000

class Player(val name: String) {
	var timeConsumedInPreviousRounds: DurationMillis = 0
		private set

	private var timeConsumedPreviouslyInCurrentRound: DurationMillis = 0

	private var resumeInstant: Instant? = null

	fun isConsuming(): Boolean = resumeInstant != null

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
		timeConsumedInPreviousRounds += timeConsumedPreviouslyInCurrentRound
		timeConsumedPreviouslyInCurrentRound = 0
	}

	fun timeConsumedAt(currentInstant: Instant): DurationMillis {
		val timeConsumedSinceLastResume = resumeInstant?.let { currentInstant - it } ?: 0
		return timeConsumedInPreviousRounds + timeConsumedPreviouslyInCurrentRound + timeConsumedSinceLastResume
	}

	fun reset() {
		timeConsumedInPreviousRounds = 0
		timeConsumedPreviouslyInCurrentRound = 0
		resumeInstant = null
	}
}

