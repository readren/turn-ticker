package readren.turnticker

typealias DurationMillis = Long
typealias Instant = Long

val MILLIS_IN_A_SECOND: Long = 1000

class Player(val name: String) {
	private var timeConsumedInPreviousRounds: DurationMillis = 0

	private var timeConsumedPreviouslyInCurrentRound: DurationMillis = 0

	private var resumeInstant: Instant? = null

	fun pause(currentInstant: Instant): Unit {
		resumeInstant?.let { ri ->
			this.timeConsumedPreviouslyInCurrentRound += currentInstant - ri
			resumeInstant = null
		}
	}

	fun resume(currentInstant: Instant): Unit {
		if (resumeInstant == null) resumeInstant = currentInstant
	}

	fun undoLastResume(): Unit {
		resumeInstant = null
	}

	fun onNewRound(currentInstant: Instant): Unit {
		pause(currentInstant)
		timeConsumedInPreviousRounds += timeConsumedPreviouslyInCurrentRound
		timeConsumedPreviouslyInCurrentRound = 0
	}

	fun timeConsumedAt(currentInstant: Instant): DurationMillis {
		val timeConsumedSinceLastResume = resumeInstant?.let { currentInstant - it } ?: 0
		return timeConsumedInPreviousRounds + timeConsumedPreviouslyInCurrentRound + timeConsumedSinceLastResume
	}

	fun timeUntilNextVisibleChange(currentInstant: Instant): DurationMillis {
		val timeConsumed = timeConsumedAt(currentInstant)
		return MILLIS_IN_A_SECOND - timeConsumed % MILLIS_IN_A_SECOND
	}

	fun reset(): Unit {
		timeConsumedInPreviousRounds = 0
		timeConsumedPreviouslyInCurrentRound = 0
		resumeInstant = null
	}
}