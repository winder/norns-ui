import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortOut
import okhttp3.OkHttpClient
import java.net.InetAddress

fun main() {
    try {
        // Refresh the screen forever.
        var default = imageFromResource("drawable/screens/eyes.png")
        var screenImage = mutableStateOf(default)
        refreshScreen(OkHttpClient(), screenImage)

        // Configure OSC
        var oscPort = OSCPortOut(InetAddress.getByName("localhost"), 10111)

        // Launch app
        window(oscPort, screenImage)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Asynchronously refresh image at url.
 */
fun refreshScreen(client: OkHttpClient, screenImage: MutableState<ImageBitmap>) {
    client.loadImageAsync("http://localhost:8889") {
        it?.let {
            screenImage.value = org.jetbrains.skija.Image.makeFromEncoded(it).asImageBitmap()
        }
        refreshScreen(client, screenImage)
    }
}

/**
 * Main window.
 */
fun window(oscPort: OSCPortOut, screenImage: MutableState<ImageBitmap>) = Window {
    @Composable
    fun enc(num: Int) {
        MusicKnob(modifier = Modifier
            .size(150.dp)
            .padding(20.dp))
        {
            try {
                oscPort.send(OSCMessage("/remote/enc/$num", listOf(it)))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Composable
    fun key(num: Int) {
        MusicButton(modifier = Modifier
            .size(100.dp)
            .padding(20.dp))
        {
            try {
                oscPort.send(OSCMessage("/remote/key/$num", listOf(it)))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    MyTheme {

        // Simulate the look of a Norns Shield.
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxWidth()
                .padding(60.dp)
        ) {
            Column {
                // Top row
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    key(1)
                    enc(1)
                }

                // Screen
                Image(
                    bitmap = screenImage.value,
                    contentDescription = "screen",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )

                // Bottom row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                    ) {
                        key(2)
                        key(3)
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        enc(2)
                        enc(3)
                    }
                }
            }
        }
    }
}

