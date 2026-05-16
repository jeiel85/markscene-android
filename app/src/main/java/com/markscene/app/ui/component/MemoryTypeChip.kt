package com.markscene.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markscene.app.core.model.MemoryType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MemoryTypeChipSection(
    selectedTypes: List<MemoryType>,
    onToggle: (MemoryType) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MemoryType.entries.forEach { type ->
            FilterChip(
                selected = type in selectedTypes,
                onClick = { onToggle(type) },
                label = { Text(type.koreanName) }
            )
        }
    }
}
