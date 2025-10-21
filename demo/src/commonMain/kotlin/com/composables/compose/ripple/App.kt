package com.composables.compose.ripple

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.SunDim
import com.composeunstyled.Button
import com.composeunstyled.Icon
import com.composeunstyled.Slider
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.Text
import com.composeunstyled.Thumb
import com.composeunstyled.ToggleSwitch
import com.composeunstyled.buildModifier
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.focusRing
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.outline
import com.composeunstyled.rememberSliderState
import com.composeunstyled.theme.ComponentInteractiveSize
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ThemeSwitcher {
    var isDark by mutableStateOf(false)
}

val LocalThemeSwitcher = compositionLocalOf { ThemeSwitcher() }

val colors = ThemeProperty<Color>("colors")
val surface = ThemeToken<Color>("surface")
val onSurface = ThemeToken<Color>("on_surface")
val primary = ThemeToken<Color>("primary")
val onPrimary = ThemeToken<Color>("on_primary")
val outline = ThemeToken<Color>("outline")
val background = ThemeToken<Color>("background")
val cardBackground = ThemeToken<Color>("card_background")

val typography = ThemeProperty<TextStyle>("typography")
val titleTextStyle = ThemeToken<TextStyle>("title")
val bodyTextStyle = ThemeToken<TextStyle>("body")

val AppTheme = buildTheme {
    name = "AppTheme"

    defaultComponentInteractiveSize = ComponentInteractiveSize(
        nonTouchInteractionSize = 32.dp,
        touchInteractionSize = 48.dp
    )

    val themeSwitcher = LocalThemeSwitcher.current
    val isDark = themeSwitcher.isDark

    val onSurfaceColor = if (isDark) Color(0xFFE4E4E7) else Color(0xFF18181B)
    val surfaceColor = if (isDark) Color(0xFF27272A) else Color.White
    val backgroundColor = if (isDark) Color(0xFF18181B) else Color(0xFFF5F5F5)
    val cardBackgroundColor = if (isDark) Color(0xFF3F3F46) else Color.White
    val outlineColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f)

    properties[colors] = mapOf(
        surface to surfaceColor,
        onSurface to onSurfaceColor,
        primary to Color(0xFF2196F3),
        onPrimary to Color.White,
        outline to outlineColor,
        background to backgroundColor,
        cardBackground to cardBackgroundColor,
    )
    properties[typography] = mapOf(
        titleTextStyle to TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        ),
        bodyTextStyle to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    )
    defaultContentColor = onSurfaceColor
}

fun Modifier.shadow(shape: Shape = RectangleShape): Modifier {
    return this then Modifier.dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = 10.dp,
            color = Color.Black.copy(alpha = 0.08f),
            spread = 0.dp,
            offset = DpOffset(0.dp, 2.dp),
            alpha = 1f,
            blendMode = DefaultBlendMode,
        )
    )
}

@Composable
fun ProvideThemeSwitcher(content: @Composable () -> Unit) {
    val themeSwitcher = remember { ThemeSwitcher() }

    CompositionLocalProvider(LocalThemeSwitcher provides themeSwitcher) {
        AppTheme {
            content()
        }
    }
}

@Composable
fun App() {
    ProvideThemeSwitcher {
        val uriHandler = LocalUriHandler.current
        val scope = rememberCoroutineScope()
        var selectedColor by remember { mutableStateOf(Color.Unspecified) }
        var bounded by remember { mutableStateOf(true) }
        var isCopied by remember { mutableStateOf(false) }
        val radiusState = rememberSliderState(initialValue = 0f, valueRange = 0f..100f)
        val focusedAlphaState = rememberSliderState(initialValue = 0.1f, valueRange = 0f..1f)
        val hoveredAlphaState = rememberSliderState(initialValue = 0.08f, valueRange = 0f..1f)
        val pressedAlphaState = rememberSliderState(initialValue = 0.1f, valueRange = 0f..1f)

        val radius = if (radiusState.value > 0f) radiusState.value.dp else Dp.Unspecified

        val rippleAlpha = Material3RippleAlpha(
            focusedAlpha = focusedAlphaState.value,
            hoveredAlpha = hoveredAlphaState.value,
            pressedAlpha = pressedAlphaState.value,
        )

        val rippleEffect = rememberRippleIndication(
            bounded = bounded,
            color = selectedColor,
            radius = radius,
            rippleAlpha = rippleAlpha,
        )

        val width = currentWindowContainerSize().width
        val orientation = if (width >= 1000.dp) StackOrientation.Horizontal else StackOrientation.Vertical
        CompositionLocalProvider(LocalIndication provides rippleEffect) {
            Column(Modifier.fillMaxSize().background(Theme[colors][background])) {
                Stack(
                    orientation = orientation,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(buildModifier {
                        add(
                            if (orientation == StackOrientation.Horizontal) {
                                Modifier.weight(1f).fillMaxHeight()
                            } else {
                                Modifier.weight(1f).fillMaxWidth()
                            }
                        )
                    }) {
                        val interactionSource = remember { MutableInteractionSource() }
                        Card(
                            interactionSource = interactionSource,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(48.dp)
                                .align(Alignment.Center)
                                .focusRing(
                                    interactionSource = interactionSource,
                                    width = 4.dp,
                                    color = Theme[colors][primary].copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .height(240.dp)
                                .widthIn(max = 480.dp)
                                .fillMaxWidth()
                        )

                        val themeSwitcher = LocalThemeSwitcher.current
                        Button(
                            onClick = { themeSwitcher.isDark = !themeSwitcher.isDark },
                            backgroundColor = Theme[colors][primary],
                            contentColor = Theme[colors][onPrimary],
                            contentPadding = PaddingValues(8.dp),
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopEnd)
                                .minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = if (themeSwitcher.isDark) Lucide.SunDim else Lucide.Moon,
                                contentDescription = null,
                            )
                        }
                    }

                    Column(
                        buildModifier {
                            add(
                                if (orientation == StackOrientation.Horizontal) {
                                    Modifier.width(480.dp).fillMaxHeight()
                                } else {
                                    Modifier
                                }
                            )
                        }
                            .background(Theme[colors][surface])
                            .outline(1.dp, Theme[colors][outline])
                            .safeDrawingPadding()
                            .padding(24.dp)
                    ) {
                        FormField(label = "Ripple Color") {
                            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                                ColorButton(
                                    color = Color(0xFF9E9E9E),
                                    isSelected = selectedColor == Color.Unspecified,
                                    onClick = { selectedColor = Color.Unspecified }
                                )
                                ColorButton(
                                    color = Color(0xFFFF5722),
                                    isSelected = selectedColor == Color(0xFFFF5722),
                                    onClick = { selectedColor = Color(0xFFFF5722) }
                                )
                                ColorButton(
                                    color = Color(0xFF2196F3),
                                    isSelected = selectedColor == Color(0xFF2196F3),
                                    onClick = { selectedColor = Color(0xFF2196F3) }
                                )
                                ColorButton(
                                    color = Color(0xFF4CAF50),
                                    isSelected = selectedColor == Color(0xFF4CAF50),
                                    onClick = { selectedColor = Color(0xFF4CAF50) }
                                )
                                ColorButton(
                                    color = Color(0xFFFFC107),
                                    isSelected = selectedColor == Color(0xFFFFC107),
                                    onClick = { selectedColor = Color(0xFFFFC107) }
                                )
                                ColorButton(
                                    color = Color(0xFF9C27B0),
                                    isSelected = selectedColor == Color(0xFF9C27B0),
                                    onClick = { selectedColor = Color(0xFF9C27B0) }
                                )
                                ColorButton(
                                    color = Color.Black,
                                    isSelected = selectedColor == Color.Black,
                                    onClick = { selectedColor = Color.Black }
                                )
                                ColorButton(
                                    color = Color.White,
                                    isSelected = selectedColor == Color.White,
                                    onClick = { selectedColor = Color.White }
                                )
                            }
                        }

                        FormField(label = "Bounded") {
                            ToggleSwitch(
                                toggled = bounded,
                                onToggled = { bounded = it },
                                modifier = Modifier.width(58.dp),
                                shape = RoundedCornerShape(100),
                                backgroundColor = if (bounded) Color(0xFF2196F3) else Color(0xFFE0E0E0),
                                contentPadding = PaddingValues(4.dp),
                                thumb = {
                                    Thumb(
                                        shape = CircleShape,
                                        color = Color.White,
                                        modifier = Modifier
                                            .outline(1.dp, Theme[colors][outline], CircleShape)
                                            .shadow(CircleShape)
                                    )
                                }
                            )
                        }

                        FormField(label = "Radius") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val radiusInteractionSource = remember { MutableInteractionSource() }
                                Slider(
                                    state = radiusState,
                                    modifier = Modifier.weight(1f),
                                    valueRange = 0f..100f,
                                    interactionSource = radiusInteractionSource,
                                    track = {
                                        Box(
                                            Modifier
                                                .fillMaxWidth(radiusState.value)
                                                .height(4.dp)
                                                .background(Theme[colors][primary], RoundedCornerShape(2.dp))
                                        )
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .background(
                                                    color = Theme[colors][primary].copy(alpha = 0.5f),
                                                    shape = RoundedCornerShape(2.dp)
                                                )
                                        )
                                    },
                                    thumb = {
                                        Box(
                                            Modifier
                                                .size(16.dp)
                                                .focusRing(
                                                    radiusInteractionSource,
                                                    width = 2.dp,
                                                    color = Theme[colors][primary],
                                                    shape = CircleShape
                                                )
                                                .outline(1.dp, Theme[colors][outline], CircleShape)
                                                .shadow(CircleShape)
                                                .background(Theme[colors][surface], CircleShape)
                                        )
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${radiusState.value.toInt()}dp",
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                                    modifier = Modifier.width(40.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth().height(48.dp).padding(bottom = 8.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text(
                                text = "Alpha",
                                style = Theme[typography][bodyTextStyle].copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        FormField(label = "Focused") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val focusedInteractionSource = remember { MutableInteractionSource() }
                                Slider(
                                    state = focusedAlphaState,
                                    modifier = Modifier.weight(1f),
                                    interactionSource = focusedInteractionSource,
                                    track = {
                                        Box(
                                            Modifier
                                                .fillMaxWidth(focusedAlphaState.value)
                                                .height(4.dp)
                                                .background(Theme[colors][primary], RoundedCornerShape(2.dp))
                                        )
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .background(
                                                    Theme[colors][primary].copy(alpha = 0.5f),
                                                    RoundedCornerShape(2.dp)
                                                )
                                        )
                                    },
                                    thumb = {
                                        Box(
                                            Modifier
                                                .size(16.dp)
                                                .focusRing(
                                                    focusedInteractionSource,
                                                    width = 2.dp,
                                                    color = Theme[colors][primary],
                                                    shape = CircleShape
                                                )
                                                .outline(1.dp, Theme[colors][outline], CircleShape)
                                                .shadow(CircleShape)
                                                .background(Theme[colors][surface], CircleShape)
                                        )
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${(focusedAlphaState.value * 100).toInt() / 100f}",
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                                    modifier = Modifier.width(40.dp)
                                )
                            }
                        }

                        FormField(label = "Hovered") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val hoveredInteractionSource = remember { MutableInteractionSource() }
                                Slider(
                                    state = hoveredAlphaState,
                                    modifier = Modifier.weight(1f),
                                    interactionSource = hoveredInteractionSource,
                                    track = {
                                        Box(
                                            Modifier
                                                .fillMaxWidth(hoveredAlphaState.value)
                                                .height(4.dp)
                                                .background(Theme[colors][primary], RoundedCornerShape(2.dp))
                                        )
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .background(
                                                    Theme[colors][primary].copy(alpha = 0.5f),
                                                    RoundedCornerShape(2.dp)
                                                )
                                        )
                                    },
                                    thumb = {
                                        Box(
                                            Modifier
                                                .size(16.dp)
                                                .focusRing(
                                                    hoveredInteractionSource,
                                                    width = 2.dp,
                                                    color = Theme[colors][primary],
                                                    shape = CircleShape
                                                )
                                                .outline(1.dp, Theme[colors][outline], CircleShape)
                                                .shadow(CircleShape)
                                                .background(Theme[colors][surface], CircleShape)
                                        )
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${(hoveredAlphaState.value * 100).toInt() / 100f}",
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                                    modifier = Modifier.width(40.dp)
                                )
                            }
                        }

                        FormField(label = "Pressed") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val pressedInteractionSource = remember { MutableInteractionSource() }
                                Slider(
                                    state = pressedAlphaState,
                                    modifier = Modifier.weight(1f),
                                    interactionSource = pressedInteractionSource,
                                    track = {
                                        Box(
                                            Modifier
                                                .fillMaxWidth(pressedAlphaState.value)
                                                .height(4.dp)
                                                .background(Theme[colors][primary], RoundedCornerShape(2.dp))
                                        )
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .background(
                                                    Theme[colors][primary].copy(alpha = 0.5f),
                                                    RoundedCornerShape(2.dp)
                                                )
                                        )
                                    },
                                    thumb = {
                                        Box(
                                            Modifier
                                                .size(16.dp)
                                                .focusRing(
                                                    pressedInteractionSource,
                                                    width = 2.dp,
                                                    color = Theme[colors][primary],
                                                    shape = CircleShape
                                                )
                                                .outline(1.dp, Theme[colors][outline], CircleShape)
                                                .shadow(CircleShape)
                                                .background(Theme[colors][surface], CircleShape)
                                        )
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${(pressedAlphaState.value * 100).toInt() / 100f}",
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                                    modifier = Modifier.width(40.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    val colorStr = when (selectedColor) {
                                        Color.Unspecified -> "Color.Unspecified"
                                        Color(0xFFFF5722) -> "Color(0xFFFF5722)"
                                        Color(0xFF2196F3) -> "Color(0xFF2196F3)"
                                        Color(0xFF4CAF50) -> "Color(0xFF4CAF50)"
                                        Color(0xFFFFC107) -> "Color(0xFFFFC107)"
                                        Color(0xFF9C27B0) -> "Color(0xFF9C27B0)"
                                        Color.Black -> "Color.Black"
                                        Color.White -> "Color.White"
                                        else -> "Color.Unspecified"
                                    }
                                    val radiusStr =
                                        if (radiusState.value > 0f) "${radiusState.value.toInt()}.dp" else "Dp.Unspecified"

                                    val code = """
val ripple = rememberMaterialRipple(
    bounded = $bounded,
    color = $colorStr,
    radius = $radiusStr,
    rippleAlpha = RippleAlpha(
        focusedAlpha = ${focusedAlphaState.value}f,
        hoveredAlpha = ${hoveredAlphaState.value}f,
        pressedAlpha = ${pressedAlphaState.value}f,
        draggedAlpha = 0.16f,
    ),
)
                                    """.trimIndent()

                                    copyToClipboard(code)
                                    isCopied = true
                                    delay(2000)
                                    isCopied = false
                                }
                            },
                            modifier = Modifier.minimumInteractiveComponentSize().fillMaxWidth(),
                            backgroundColor = Theme[colors][primary],
                            contentColor = Theme[colors][onPrimary],
                            shape = RoundedCornerShape(8.dp),
                            enabled = isCopied.not(),
                        ) {
                            Text(
                                text = if (isCopied) "Copied" else "Copy to Clipboard",
                                style = Theme[typography][bodyTextStyle],
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = { uriHandler.openUri("https://github.com/composablehorizons/compose-material-ripple") },
                            backgroundColor = Color.Transparent,
                            contentColor = Theme[colors][primary],
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.minimumInteractiveComponentSize().fillMaxWidth(),
                        ) {
                            Text(
                                text = "View on GitHub",
                                style = Theme[typography][bodyTextStyle]
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun Card(shape: Shape, modifier: Modifier = Modifier, interactionSource: MutableInteractionSource) {
    Button(
        backgroundColor = Theme[colors][cardBackground],
        onClick = { },
        interactionSource = interactionSource,
        shape = shape,
        modifier = modifier
            .shadow(shape)
            .outline(1.dp, Theme[colors][outline], shape)
    ) {

    }
}

@Composable
private fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = Theme[typography][bodyTextStyle],
            modifier = Modifier.width(100.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f)) {
            content()
        }
    }
}

@Composable
private fun ColorButton(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        backgroundColor = color,
        shape = CircleShape,
        modifier = Modifier.minimumInteractiveComponentSize()
            .outline(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Theme[colors][primary] else Theme[colors][outline],
                shape = CircleShape
            )
    ) {

    }
}

expect suspend fun copyToClipboard(text: String)
