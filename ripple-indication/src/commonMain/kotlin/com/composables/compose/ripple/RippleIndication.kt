package com.composables.compose.ripple

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.RippleAlpha as MaterialRippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.Dp

@Composable
fun rememberRippleIndication(
    color: Color = Color.Unspecified,
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    rippleAlpha: RippleAlpha = Material3RippleAlpha(),
): IndicationNodeFactory {
    val colorState = rememberUpdatedState(color)
    val rippleAlphaState = rememberUpdatedState(rippleAlpha)
    return remember(color, bounded, radius, rippleAlpha) {
        MaterialRippleNodeFactory(
            bounded = bounded,
            radius = radius,
            color = { colorState.value },
            rippleAlpha = {
                val current = rippleAlphaState.value
                MaterialRippleAlpha(
                    draggedAlpha = current.draggedAlpha,
                    focusedAlpha = current.focusedAlpha,
                    hoveredAlpha = current.hoveredAlpha,
                    pressedAlpha = current.pressedAlpha,
                )
            }
        )
    }
}

@Immutable
data class RippleAlpha(
    val draggedAlpha: Float,
    val focusedAlpha: Float,
    val hoveredAlpha: Float,
    val pressedAlpha: Float,
)

/**
 * The default MaterialRippleAlpha from Material 3
 *
 * Values are taken from Material 3 Compose
 */
val Material3DefaultRipple = RippleAlpha(
    draggedAlpha = 0.16f,
    focusedAlpha = 0.1f,
    hoveredAlpha = 0.08f,
    pressedAlpha = 0.1f,
)

fun Material3RippleAlpha(
    draggedAlpha: Float = 0.16f,
    focusedAlpha: Float = 0.1f,
    hoveredAlpha: Float = 0.08f,
    pressedAlpha: Float = 0.1f,
): RippleAlpha {
    return RippleAlpha(
        draggedAlpha = draggedAlpha,
        focusedAlpha = focusedAlpha,
        hoveredAlpha = hoveredAlpha,
        pressedAlpha = pressedAlpha,
    )
}

internal class MaterialRippleNodeFactory(
    val bounded: Boolean = true,
    val radius: Dp,
    val color: ColorProducer,
    val rippleAlpha: () -> MaterialRippleAlpha,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return createRippleModifierNode(interactionSource, bounded, radius, color, rippleAlpha)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MaterialRippleNodeFactory

        if (bounded != other.bounded) return false
        if (radius != other.radius) return false
        if (color != other.color) return false
        if (rippleAlpha != other.rippleAlpha) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bounded.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + rippleAlpha.hashCode()
        return result
    }
}