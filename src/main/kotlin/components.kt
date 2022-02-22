import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import java.awt.event.MouseEvent

/**
 * MyTheme disables the click animation.
 */
@Composable
fun MyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme {
        CompositionLocalProvider(
            LocalIndication provides rememberRipple(radius = 0.dp),
            content = content,
        )
    }
}

@Composable
fun MusicButton(
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    var pressed by remember {
        mutableStateOf(false)
    }
    var lastEvent by remember {
        mutableStateOf<MouseEvent?>(null)
    }

    Image(
        bitmap = imageFromResource("drawable/button.png"),
        contentDescription = "Music Button",
        modifier = modifier
            // mouse events aren't directly accessible, so use this weird pattern.
            // mouse down event
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        var event = awaitPointerEvent().mouseEvent
                        if (event?.button == 1) {
                            lastEvent = event
                            pressed = lastEvent?.button != 0
                            onClick(1)
                        }
                    }
                }
            }
            // mouse up event
            .clickable {
                if (lastEvent?.button == MouseEvent.BUTTON1) {
                    lastEvent = null
                    pressed = false
                    onClick(0)
                }
            }
            /*
            // maybe this can replace the mouse events?
            .toggleable(value = false) {
                pressed = it
                onClick(if (it)  1 else 0)
            }
             */
            .rotate(if (pressed) 180f else 0f)
    )
}

@Composable
fun MusicKnob(
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit
) {
    var rotation by remember {
        mutableStateOf(0f)
    }

    Image(
        bitmap = imageFromResource("drawable/knob.png"),
        contentDescription = "Music knob",
        modifier = modifier
            // workaround for missing pointerInteropFilter, this only gets one axis
            // using pointerInput directly could work but is more complicated
            // https://github.com/JetBrains/compose-jb/issues/129#issuecomment-784149646
            .draggable(
                startDragImmediately = true,
                orientation = Orientation.Vertical,
                state = rememberDraggableState {
                    val d = it / 2
                    rotation += it * 10
                    println(d)
                    onValueChange(d)
                }
            )
            .rotate(rotation)
    )
}
