package org.zjh.project


import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import timber.log.Timber

@Composable
fun ScaffoldHome(navController: NavHostController) {

    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("主页", "我喜欢的", "设置")
    val itemsMap = listOf("home", "history", "setting")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("主页")
                },
            )
        },
        bottomBar = {
            BottomNavigation {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = {
                            when (index) {
                                0 -> Icon(
                                    Icons.Filled.Home,
                                    contentDescription = null,
                                    tint = Color.White
                                )

                                1 -> Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.White
                                )

                                else -> Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        },
                        label = { Text(item, color = Color.White) },
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
    fun getAllUri() {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val allEntries = sharedPref.all.entries
        val loadedUris = allEntries.filter { it.value is String }
            .map { Pair(it.key, Uri.parse(it.value as String)) }
        uris = loadedUris


    }

    // 初始化时加载数据
    LaunchedEffect(Unit) {
        getAllUri()
    }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        uris.forEach { i ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(200.dp)
                    .clickable {
                        navController.navigate("home?uri=${i.first}")
                    }

            ) {

                Image(

                    painter = rememberAsyncImagePainter(i.second),
                    contentDescription = i.first,
                    contentScale = ContentScale.Crop // 设置图片缩放类型为裁剪以填满空间
                )
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
    MaterialTheme() {
        ScaffoldHome(navController)
    }
}