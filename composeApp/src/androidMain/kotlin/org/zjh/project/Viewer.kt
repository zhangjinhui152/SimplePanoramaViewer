package org.zjh.project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
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
    fun saveUriToString(context: Context, key: String, uri: Uri) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flags)
        Toast.makeText(context, "已加入持久化😠", Toast.LENGTH_SHORT).show()
        editor.putString(key, uri.toString())
        editor.apply()

    }

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

            this.pl!!.onCreate()
            val panorama = PLSphericalPanorama()
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
        LaunchedEffect(Unit) {
            Timber.d("Debug tempUri !!!!!!!!!")
            val sharedPref = context!!.getSharedPreferences("tempUri", Context.MODE_PRIVATE)

            Timber.d("Debug tempUri $sharedPref")
            val tempUri = sharedPref.getString("uri", null)  // 获取存储的 URI
            // 执行下一步操作
            Timber.d("Debug tempUri $tempUri")
            tempUri?.let {
                getBitmapFromUriAndSetBitmap(Uri.parse(it))
                context!!.deleteSharedPreferences("tempUri")
            }
            context!!.deleteSharedPreferences("tempUri")
        }
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
        var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments()
        ) { uris: List<Uri> ->
            // 处理选择的多个图片 URI
            if (uris.isNotEmpty()) {
                selectedImageUris = uris
                // 这里可以进一步处理，比如显示多张图片等
            }
        }

// 创建一个按钮来触发打开相册操作
        Button(onClick = { launcher.launch(arrayOf("image/*")) }) {
            Text(text = "选择图片")
        }
        selectedImageUris.let {
            if (it.isNotEmpty()) {
                val uri = it[0]
                println(uri)
                saveUriToString(context!!, uri.toString(), uri)
                Timber.tag("imageUrl").d("OpenAlbumAndDisplayImage:$uri ")
                getBitmapFromUriAndSetBitmap(uri)
            }
        }
    }
}