# Compose Ripple Indication

Use the Material ripple effect in any Compose UI design system and app.

## Installation

```kotlin title="app/build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:ripple-indication:x.x.x")
}
```

## Quick Start

Wrap the contents of your app with a Local

```kotlin
import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.composables.compose.ripple.rememberRippleIndication

@Composable
fun App() {
    CompositionLocalProvider(LocalIndication provides rememberRippleIndication()) {
        // app contents here
    }
}
```

or use the `defaultIndication` property in your Compose Unstyled theme:

```kotlin
import androidx.compose.runtime.Composable
import com.composables.compose.ripple.rememberRippleIndication
import com.composeunstyled.theme.buildTheme

val AppTheme = buildTheme {
    defaultIndication = rememberRippleIndication()
}

@Composable
fun App() {
    AppTheme {
        // app contents here
    }
}
```

and you are set. Your app contents will now use the ripple indication.

By default, the effect will use Material 3's default alpha values and a boring gray color. Continue reading to learn how
to spicy it up:

## Customization Options

### How to change the ripple's color

Use the `color` parameter when creating the ripple. Note that the alpha of the color will be affected by the [
`rippleAlpha`](#how-to-change-the-ripples-alpha) parameter:

```kotlin
rememberRippleIndication(color = Color.Blue)
```

### How to change the ripple's alpha

Use the `rippleAlpha` parameter when creating the ripple. The default values are taken from the Material 3 Compose:

```kotlin
rememberRippleIndication(
    rippleAlpha = RippleAlpha(
        draggedAlpha = 0.16f,
        focusedAlpha = 0.1f,
        hoveredAlpha = 0.08f,
        pressedAlpha = 0.1f,
    )
)
```

### Bound vs unbounded ripple

You can specify whether the ripple is bound to the target layout. Unbounded ripples always animate from the target layout center, bounded ripples animate from the touch position

By default, ripple are bounded to the layout, and animate from the touch position. To override this and animate from the layout center, pass `false` to the `bounded` parameter:

```kotlin
rememberRippleIndication(bounded = false)
```

## How is this different to Google's material-ripple package?

The official Google ripple package is too 'raw'. You cannot use it out of the box without digging into code and try to
figure out how to use the API.

We provide a single `rememberRippleIndication()` function, which you can just plug into your existing design system,
without having to worry about the details.

## Build your own component library

Use [Compose Unstyled](https://composables.com?ref=ripple) to create your own Compose component library in any platform.

## Contributing

We are currently accepting contributions in the form of bug reports and feature requests, in the form of Github issues.
