package readren.turnticker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
//import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NameEntry(
	names: List<String>,
	isNameValid: (String) -> Boolean,
	onNameAdded: (String) -> Unit,
	onNameRemoved: (String) -> Unit,
	modifier: Modifier = Modifier,
	addButtonText: String = "Add",
	placeholder: String = "Enter a name"
) {
	var currentName by remember { mutableStateOf("") }
	var nameError by remember { mutableStateOf(false) }
//	val focusManager = LocalFocusManager.current

	fun addName() {
		if (currentName.isNotBlank() && isNameValid(currentName)) {
			onNameAdded(currentName)
			currentName = ""
//			focusManager.clearFocus()
		} else {
			nameError = true
		}
	}

	Column(modifier = modifier) {
		// Input row
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			TextField(
				value = currentName,
				onValueChange = {
					currentName = it
					nameError = false // Reset error when typing
				},
				modifier = Modifier.weight(1f),
				placeholder = { Text(placeholder) },
				isError = nameError,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = KeyboardActions({ addName() }),
				singleLine = true
			)

			Spacer(modifier = Modifier.width(8.dp))

			Button(
				onClick = { addName() },
				enabled = currentName.isNotBlank(),
			) {
				Text(addButtonText)
			}
		}

		// Error message
		if (nameError) {
			Text(
				text = "Invalid name",
				color = MaterialTheme.colorScheme.error,
				fontSize = 12.sp,
				modifier = Modifier.padding(start = 16.dp, top = 4.dp)
			)
		}

		// Names list
		if (names.isNotEmpty()) {
			LazyColumn(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 8.dp)
			) {
				items(names) { name ->
					NameItem(
						name = name,
						onRemove = { onNameRemoved(name) }
					)
				}
			}
		}
	}
}

@Composable
private fun NameItem(
	name: String,
	onRemove: () -> Unit
) {
	Row(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.secondaryContainer)
			.fillMaxWidth()
			.padding(start = 6.dp, top = 2.dp, bottom = 2.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = name,
			color = MaterialTheme.colorScheme.onSecondaryContainer,
			modifier = Modifier.weight(1f),
			style = MaterialTheme.typography.bodyLarge
		)
		IconButton(onClick = onRemove) {
			Icon(
				imageVector = Icons.TwoTone.Delete,
				contentDescription = "Remove",
			)
		}
	}
}

@Preview
@Composable
fun NameEntryPreview() {

	NameEntry(
		modifier = Modifier,
		names = listOf("first", "second"),
		isNameValid = { _ -> true },
		onNameAdded = { _ -> },
		onNameRemoved = { _ -> },
		addButtonText = "Add",
		placeholder = "Enter a name"
	)
}