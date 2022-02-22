import okhttp3.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Helper to load an image from a URL.
 * onComplete is called with the bytes, or null on failures.
 */
fun OkHttpClient.loadImageAsync(url: String, onComplete: (ByteArray?) -> Unit) {
    val request = Request.Builder().url(url).build()

    newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onComplete(null)
        }
        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    onComplete(null)
                    return
                }

                response.body?.let{ body ->
                    val bufferedImage: BufferedImage = ImageIO.read(body.byteStream())
                    ByteArrayOutputStream().use { out ->
                        ImageIO.write(bufferedImage, "png", out)
                        response.body?.close()
                        onComplete(out.toByteArray())
                    }
                }
            }
        }
    })
}

