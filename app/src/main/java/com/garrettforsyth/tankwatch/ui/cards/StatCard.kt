package com.garrettforsyth.tankwatch.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.ui.components.LabeledIcon
import com.garrettforsyth.tankwatch.ui.model.StatType
import com.garrettforsyth.tankwatch.ui.theme.Dimensions.Padding
import com.garrettforsyth.tankwatch.ui.theme.TankWatchTheme

@Composable
fun StatCard(
    value: String,
    type: StatType,
    modifier: Modifier = Modifier
) {
    val (label, icon) = when (type) {
        StatType.Min -> stringResource(R.string.min) to Icons.Outlined.ArrowDownward
        StatType.Max -> stringResource(R.string.max) to Icons.Outlined.ArrowUpward
        StatType.Average -> stringResource(R.string.average) to Icons.Outlined.DragHandle
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Padding.small)
        ) {
            LabeledIcon(label, icon)
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatCardPreview() {
    TankWatchTheme {
        StatCard(
            "25.2",
            StatType.Average
        )
    }
}
