package org.zjh.project

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
import com.panoramagl.PLTexture
import timber.log.Timber


class Viewer {
    private var pl: PLManager? = null
    lateinit var currentView: CustomRelativeLayout
    private var context: Context? = null
    private fun setPlManagerAndContext(pl: PLManager, context: Context) {
        this.pl = pl
        this.context = context
    }

    private var count = 0

    private fun getBitmapFromUriAndSetBitmap(uri: Uri): Unit {
        Timber.d("Debug message pl ${this.pl}")
        Timber.d("Debug message context. ${this.context}")


        val scaledBitmap = getSbitmap(uri)
        Timber.d("Debug message $scaledBitmap");
        if (count != 0) {
            this.pl = null
            this.pl = PLManager(context!!)
            Timber.d("this.pl ${this.pl}")
            this.pl?.setContentView(currentView)
            currentView.plManager = this.pl!!
            this.pl!!.onCreate()
            val panorama = PLSphericalPanorama()
            panorama.camera.lookAt(30.0f, 90.0f)
            val plImage = PLImage(scaledBitmap, false)
            panorama.setImage(plImage)
            this.pl?.panorama = panorama

        } else {
            val panorama = PLSphericalPanorama()
            Timber.d("Debug message 第one次 ${panorama.texturesLength()}");
            Timber.d("Debug message 第one次");
            this.pl!!.onCreate()
            panorama.camera.lookAt(30.0f, 90.0f)
            val plImage = PLImage(scaledBitmap, false)
            panorama.setImage(plImage)
            this.pl?.panorama = panorama

        }
        Timber.d("Debug message $uri");
        Timber.d("Debug message ${this.pl?.panorama}");
        Timber.d("Debug message isRendererCreated ${this.pl?.isRendererCreated}");
        Timber.d("Debug message context ${this.context}");
        count++


    }

    private fun getSbitmap(uri: Uri): Bitmap {
        val originalBitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
        // 缩放图片
        // 计算目标尺寸
        val maxWidth = 8192
        val maxHeight = 8192

        val scaledBitmap =
            if (originalBitmap.width > maxWidth || originalBitmap.height > maxHeight) {
                val scaleWidth = maxWidth.toFloat() / originalBitmap.width
                val scaleHeight = maxHeight.toFloat() / originalBitmap.height
                val scaleFactor = if (scaleWidth < scaleHeight) scaleWidth else scaleHeight
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (originalBitmap.width * scaleFactor).toInt(),
                    (originalBitmap.height * scaleFactor).toInt(),
                    true
                )
            } else {
                originalBitmap
            }
        return scaledBitmap
    }


    @SuppressLint("ViewConstructor")
    class CustomRelativeLayout(
        context: Context, attrs: AttributeSet? = null, var plManager: PLManager
    ) : RelativeLayout(context, attrs) {


        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean {
            Timber.d("Debug ClickableViewAccessibility ${this.plManager}")
            return this.plManager.onTouchEvent(event)
        }



    }

    @SuppressLint("SetTextI18n")
    @Composable
    fun CustomView() {
        lateinit var plManager: PLManager

        //widget.Button
        AndroidView(
            factory = { ctx ->
                plManager = PLManager(ctx)
                this.pl = plManager
                this.setPlManagerAndContext(plManager, ctx)
                //Here you can construct your View
                currentView = CustomRelativeLayout(ctx, plManager = this.pl!!)

                Timber.d("Debug message pl ${this.pl}")
                Timber.d("Debug message context. ${ctx}")
                this.pl?.setContentView(currentView)
                currentView
            }, modifier = Modifier.padding(8.dp)

        )

    }

    @Composable
    fun OpenAlbumAndDisplayImage() {
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedImageUri = uri
        }
        // 创建一个按钮来触发打开相册操作
        Button(onClick = { launcher.launch("image/*") }) {
            Text(text = "打开相册")
        }
//        Text(selectedImageUri.toString())
        selectedImageUri?.let { uri ->
            // 使用Coil或其他图片加载库来显示选中的图片
            println(uri)
            Timber.tag("imageUrl").d("OpenAlbumAndDisplayImage:$uri ")
            getBitmapFromUriAndSetBitmap(uri)
        }
    }
}