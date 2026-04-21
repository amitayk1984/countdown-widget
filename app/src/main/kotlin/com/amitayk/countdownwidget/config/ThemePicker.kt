package com.amitayk.countdownwidget.config

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amitayk.countdownwidget.data.WidgetTheme
import com.amitayk.countdownwidget.data.WidgetThemes

private val ACCENT = Color(0xFFBB86FC)

@Composable
fun ThemePicker(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "WIDGET THEME",
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.padding(bottom = 10.dp),
        )

        // 4-column grid via chunked rows (avoids nested-scroll issues with LazyVerticalGrid)
        val rows = WidgetThemes.all.chunked(4)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    row.forEach { theme ->
                        ThemeCell(
                            theme = theme,
                            selected = selectedId == theme.id,
                            onClick = { onSelect(theme.id) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    // Fill trailing empty cells so columns stay aligned
                    repeat(4 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeCell(
    theme: WidgetTheme,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellShape = RoundedCornerShape(14.dp)
    val bgBrush: Brush = if (theme.backgroundIsGradient
        && theme.gradientStartHex != null
        && theme.gradientEndHex != null
    ) {
        Brush.verticalGradient(
            colors = listOf(
                hexToColor(theme.gradientStartHex),
                hexToColor(theme.gradientEndHex),
            )
        )
    } else {
        SolidColor(hexToColor(theme.backgroundColorHex))
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Mini widget preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(cellShape)
                .background(bgBrush)
                .then(
                    if (selected)
                        Modifier.border(2.dp, ACCENT, cellShape)
                    else if (theme.borderColorHex != null)
                        Modifier.border(1.dp, hexToColor(theme.borderColorHex), cellShape)
                    else
                        Modifier
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "42",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = hexToColor(theme.primaryTextColorHex),
                    lineHeight = 16.sp,
                )
                Text(
                    text = "days",
                    fontSize = 7.sp,
                    color = hexToColor(theme.secondaryTextColorHex),
                    lineHeight = 8.sp,
                )
            }
        }

        Spacer(Modifier.height(3.dp))

        // Theme name label
        Text(
            text = theme.name,
            fontSize = 8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = if (selected) ACCENT else Color.White.copy(alpha = 0.55f),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Parse a #RRGGBB or #AARRGGBB hex string into a Compose [Color]. */
private fun hexToColor(hex: String): Color =
    try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        Color.Gray
    }
