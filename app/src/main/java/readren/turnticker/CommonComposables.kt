package readren.turnticker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun Section(modifier: Modifier = Modifier.padding(top = 8.dp, bottom = 8.dp), content: @Composable ColumnScope.() -> Unit) {
	Surface(
		color = MaterialTheme.colorScheme.surface,
		tonalElevation = 16.dp,
		shadowElevation = 8.dp,
		modifier = Modifier
			.clip(MaterialTheme.shapes.large)
			.fillMaxWidth()
	) {
		Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
	}
}

@Composable
fun CheckboxContainer(text: String, content: @Composable () -> Unit) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.clip(MaterialTheme.shapes.small)
			.border(width = 2.dp, color = MaterialTheme.colorScheme.outline)
			.background(MaterialTheme.colorScheme.secondary)
	) {
		Text(text, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.padding(start = 8.dp))
		content()
	}
}
