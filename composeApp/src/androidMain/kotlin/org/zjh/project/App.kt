package org.zjh.project

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_SMS
import android.Manifest.permission_group.READ_MEDIA_AURAL
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.zjh.project.ui.theme.backgroundLight
import timber.log.Timber

@Composable
fun ScaffoldHome(navController: NavHostController) {

    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("主页", "我喜欢的", "设置")
    val itemsMap = listOf("home", "history", "setting")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("全景查看器") },
                backgroundColor = MaterialTheme.colorScheme.background
            )
        },
        bottomBar = {
            NavigationBar ( ){

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (index) {
                                0 -> Icon(
                                    Icons.Filled.Home,
                                    contentDescription = null,
//                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(5.dp)

                                )

                                1 -> Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null,
//                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(5.dp)
                                )

                                else -> Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = null,
//                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        },
                        label = { Text(item, color = MaterialTheme.colorScheme.tertiary) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(itemsMap[selectedItem])
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { Viewer360(navController) }
            composable("history") { History(navController) }
            composable("setting") { Setting() }

        }

    }
}

@Composable
fun Viewer360(navController: NavHostController) {
    val viewer = Viewer()


    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        viewer.OpenAlbumAndDisplayImage()
        viewer.CustomView()
    }
}

@Composable
fun History(navController: NavHostController) {

    val context = LocalContext.current
    var uris by remember { mutableStateOf<List<Pair<String, Uri>>>(emptyList()) }
    val shouldShowRequestPermissionRationale = remember {
        mutableStateOf(false)
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
//            Toast.makeText(context, "权限已授予", Toast.LENGTH_SHORT).show()
        } else {
            if (shouldShowRequestPermissionRationale.value) {
                Toast.makeText(context, "请在设置中授予读取外部存储的权限", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAllUri() {
        requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val allEntries = sharedPref.all.entries
        val loadedUris = allEntries.filter { it.value is String }
            .map { Pair(it.key, Uri.parse(it.value as String)) }
        uris = loadedUris
        Log.d("TAG", "getAllUri: $uris")

    }

    // 初始化时加载数据
    LaunchedEffect(Unit) {
        getAllUri()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(uris.size) { index ->
            val (uri, imageUri) = uris[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(200.dp)
                    .clickable {
                        navController.navigate("home?uri=$uri")
                    },
                shape = RoundedCornerShape(8.dp)
            ) {
                Image(
                     painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = uri,
                    contentScale = ContentScale.Crop // 设置图片缩放类型为裁剪以填满空间
                )
            }
        }
        item {
            Button(
                onClick = { context.deleteSharedPreferences("MyPrefs") },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "清除列表")
            }
        }
    }
}

@Composable
fun Setting() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Setting")
    }
}


@Composable
@Preview
fun App() {

    val navController = rememberNavController()
    Timber.plant(Timber.DebugTree())
    MaterialTheme(colorScheme =  org.zjh.project.ui.theme.lightScheme) {
        ScaffoldHome(navController)
    }
}