package hr.wortex.otpstudent.ui.nav

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun assetToImageBitmap(fileName: String): ImageBitmap? {
    val context = LocalContext.current
    return try {
        val inputStream = context.assets.open(fileName)
        BitmapFactory.decodeStream(inputStream).asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}