# ComposeMusic

<p align="center">
    <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
    <img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language">
    <img src="https://img.shields.io/badge/Architecture-Compose-purple.svg" alt="Architecture">
</p>


## 项目简介

此项目基 [NeteaseCloudMusicApi](https://gitlab.com/Binaryify/neteasecloudmusicapi) 项目自部署作为API, 使用 Jetpack Compose构建的音乐播放器应用, 采用现代 Android 开发技术栈，通过实践来深入理解
Compose 声明式 UI 的开发模式

此项目需在 di/AppModule 下配置 NeteaseCloudMusicApi 的后端地址

## 功能展示

|            密码登录            |            扫码登录            |         个人页面         |         收藏内容         |
|:--------------------------:|:--------------------------:|:--------------------:|:--------------------:|
| ![密码登录](./images/密码登录.png) | ![扫码登录](./images/扫码登录.png) | ![我的](images/我的.png) | ![收藏](images/收藏.png) |


|            专辑推荐            |            歌单推荐            |            歌曲推荐            |            艺术家推荐             |
|:--------------------------:|:--------------------------:|:--------------------------:|:----------------------------:|
| ![推荐-专辑](images/推荐-专辑.png) | ![推荐-歌单](images/推荐-歌单.png) | ![推荐-歌曲](images/推荐-歌曲.png) | ![推荐-艺术家](images/推荐-艺术家.png) |


|         搜索界面         |           搜索结果           |           播放列表           |           播放界面           |
|:--------------------:|:------------------------:|:------------------------:|:------------------------:|
| ![搜索](images/搜索.png) | ![搜索结果](images/搜索结果.png) | ![播放列表](images/播放列表.png) | ![播放界面](images/播放界面.png) |


|          榜单          |          电台          |           艺术家主页            |
|:--------------------:|:--------------------:|:--------------------------:|
| ![榜单](images/榜单.png) | ![电台](images/电台.png) | ![艺术家主页](images/艺术家主页.png) |


|           歌单内容           |           歌曲评论           |             二级评论             |
|:------------------------:|:------------------------:|:----------------------------:|
| ![歌单内容](images/歌单内容.png) | ![歌曲评论](images/歌曲评论.png) | ![歌曲二级评论](images/歌曲二级评论.png) |


## 开发环境

- Android Studio Ladybug | 2024.2.1
- Android Gradle Plugin Version 8.1.3
- Gradle Version 8.0
- JDK 17
