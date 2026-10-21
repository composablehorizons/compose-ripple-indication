package com.composables.compose.ripple

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ControlledComposition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class RippleIndicationTest {
    @Test
    fun defaultRadiusKeepsFactoryAcrossRecompositions() {
        val generation = mutableStateOf(0)
        withComposition({
            generation.value
            rememberRippleIndication()
        }) { recompose, current ->
            val initial = current()
            generation.value++
            recompose()
            assertSame(initial, current())
        }
    }

    @Test
    fun colorAndAlphaChangesKeepFactory() {
        val color = mutableStateOf(Color.Red)
        val alpha = mutableStateOf(Material3RippleAlpha())
        withComposition({
            rememberRippleIndication(color = color.value, rippleAlpha = alpha.value)
        }) { recompose, current ->
            val initial = current()
            color.value = Color.Blue
            recompose()
            assertSame(initial, current())
            alpha.value = Material3RippleAlpha(pressedAlpha = 0.5f)
            recompose()
            assertSame(initial, current())
        }
    }

    @Test
    fun boundsAndRadiusChangesReplaceFactory() {
        val bounded = mutableStateOf(true)
        val radius = mutableStateOf(10.dp)
        withComposition({
            rememberRippleIndication(bounded = bounded.value, radius = radius.value)
        }) { recompose, current ->
            val initial = current()
            bounded.value = false
            recompose()
            assertNotSame(initial, current())
            val unbounded = current()
            radius.value = 20.dp
            recompose()
            assertNotSame(unbounded, current())
        }
    }

    private fun withComposition(
        content: @Composable () -> IndicationNodeFactory,
        test: (recompose: () -> Unit, current: () -> IndicationNodeFactory) -> Unit,
    ) {
        val recomposer = Recomposer(EmptyCoroutineContext)
        val composition = ControlledComposition(object : AbstractApplier<Unit>(Unit) {
            override fun insertTopDown(index: Int, instance: Unit) = Unit
            override fun insertBottomUp(index: Int, instance: Unit) = Unit
            override fun remove(index: Int, count: Int) = Unit
            override fun move(from: Int, to: Int, count: Int) = Unit
            override fun onClear() = Unit
        }, recomposer)
        var compositions = 0
        lateinit var factory: IndicationNodeFactory
        try {
            composition.composeContent {
                compositions++
                factory = content()
            }
            composition.applyChanges()
            Snapshot.notifyObjectsInitialized()
            test({
                Snapshot.sendApplyNotifications()
                val previousCompositions = compositions
                composition.invalidateAll()
                if (composition.recompose()) composition.applyChanges()
                check(compositions > previousCompositions) { "The content must recompose before checking its factory" }
                Snapshot.notifyObjectsInitialized()
            }, { factory })
        } finally {
            composition.dispose()
            recomposer.cancel()
        }
    }
}
