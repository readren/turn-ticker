package readren.turnticker

import androidx.compose.foundation.background
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerInput(
	label: String,
	modifier: Modifier = Modifier,
	initialValue: Int = 0,
	initialUnit: TimeUnit = TimeUnit.MINUTES,
	onTimeChange: (Long, TimeUnit) -> Unit
) {
	var timeValue by remember { mutableStateOf(initialValue.toString()) }
	var selectedUnit by remember { mutableStateOf(initialUnit) }
	var unitMenuExpanded by remember { mutableStateOf(false) }


	fun notifyChange() {
		val numericValue = if (timeValue.isEmpty()) 0L else timeValue.toLong()
		onTimeChange(numericValue, selectedUnit)
	}

	Row(
		modifier = modifier
			.fillMaxWidth()
			.height(IntrinsicSize.Min),
		verticalAlignment = Alignment.CenterVertically
	) {
		TextField(
			value = timeValue,
			modifier = Modifier.weight(1f),
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

		Spacer(modifier = Modifier.width(4.dp))

		Box(
			Modifier
				.fillMaxHeight()
				.background(
					color = MaterialTheme.colorScheme.surfaceVariant,
					shape = MaterialTheme.shapes.small
				),
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

//		ExposedDropdownMenuBox(
//			modifier = Modifier,
//			expanded = unitMenuExpanded,
//			onExpandedChange = { unitMenuExpanded = !unitMenuExpanded }
//		) {
//				TextField(
//					readOnly = true,
//					value = selectedUnit.displayName,
//					onValueChange = { },
//					trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitMenuExpanded) },
//					modifier = Modifier
//						.menuAnchor(MenuAnchorType.PrimaryNotEditable, true) // TODO try other arguments here
//						.width(120.dp)
//				)
//			ExposedDropdownMenu(
//				expanded = unitMenuExpanded,
//				onDismissRequest = { unitMenuExpanded = false }
//			) {
//				TimeUnit.entries.forEach { unit ->
//					DropdownMenuItem(
//						text = { Text(unit.displayName) },
//						onClick = {
//							selectedUnit = unit
//							unitMenuExpanded = false
//							notifyChange()
//						}
//					)
//				}
//			}
//		}
	}
}

@Preview
@Composable
fun TimerInputPreview() {
	TimerInput("Initial time") { t, u -> }
}