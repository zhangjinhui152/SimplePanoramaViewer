# SimplePanoramaViewer
- 使用 kotlin 的简单全景查看器SimplePanoramaViewer
- 使用了https://github.com/hannesa2/panoramaGL 这个库
- 使用andorid studio 构建
- 使用 material design3
- 可用的release apk在composeApp\release\composeApp-release.apk 里面



#### This is a Kotlin Multiplatform project targeting Android.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…