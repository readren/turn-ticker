package readren.turnticker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TimeUnit(val displayName: String, val millis: Int) {
	SECONDS("Seconds", 1000),
	MINUTES("Minutes", 60 * 1000),
	HOURS("Hours", 24 * 60 * 1000)
}

@Composable
fun TimerInput(
	label: String,
	initialValue: Long,
	modifier: Modifier = Modifier,
	initialUnit: TimeUnit = TimeUnit.MINUTES,
	onTimeChange: (Long, TimeUnit) -> Unit
) {
	var selectedUnit by remember { mutableStateOf(initialUnit) }
	var timeValue by remember { mutableStateOf((initialValue/(selectedUnit.millis)).toString()) }
	var unitMenuExpanded by remember { mutableStateOf(false) }


	fun notifyChange() {
		val numericValue = if (timeValue.isEmpty()) 0L else timeValue.toLong()
		onTimeChange(numericValue, selectedUnit)
	}

	Row(
		modifier = modifier
			.clip(MaterialTheme.shapes.medium)
			.border(width = 2.dp, color = MaterialTheme.colorScheme.outline)
			.fillMaxWidth()
			.height(IntrinsicSize.Min),
		verticalAlignment = Alignment.CenterVertically,
	) {
		TextField(
			value = timeValue,
			modifier = Modifier.weight(1f).fillMaxHeight(),
			textStyle = TextStyle(fontSize = 20.sp),
			onValueChange = { newValue ->
				// Only allow positive integers (no decimals, no negatives)
				if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
					timeValue = newValue
					notifyChange()
				}
			},
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true,
			label = { Text(label, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary) }
		)

		Box(
			Modifier
				.fillMaxHeight()
				.background(color = MaterialTheme.colorScheme.surfaceVariant),
			contentAlignment = Alignment.Center
		) {
			Column {
				Icon(
					Icons.TwoTone.ArrowDropDown,
					null,
					Modifier.align(Alignment.CenterHorizontally).rotate(if (unitMenuExpanded) 180f else 0f),
					MaterialTheme.colorScheme.onSurface
				)
				Text(
					text = selectedUnit.displayName,
					fontSize = 20.sp,
					modifier = Modifier.clickable { unitMenuExpanded = true }.padding(2.dp, 0.dp),
					color = MaterialTheme.colorScheme.onSurface
				)
			}

			DropdownMenu(
				expanded = unitMenuExpanded,
				onDismissRequest = { unitMenuExpanded = false }
			) {
				TimeUnit.entries.forEach { timeUnit ->
					DropdownMenuItem(
						text = { Text(timeUnit.displayName) },
						onClick = {
							selectedUnit = timeUnit
							unitMenuExpanded = false
							notifyChange()
						}
					)
				}
			}
		}
	}
}

@Preview
@Composable
fun TimerInputPreview() {
	TimerInput("Initial time", 1234) { t, u -> }
}