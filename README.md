# AbandonScanner

一个Android应用程序，用于扫描和记录废弃的条形码，并在检测到废弃条形码时发出警报。

## 功能特性

- **扫描废弃条形码**: 扫描并记录废弃的条形码到本地数据库
- **检查条形码**: 扫描条形码并检查是否在废弃列表中
- **导入功能**: 从txt文件导入废弃条形码列表
- **清除功能**: 清除所有废弃条形码记录
- **多语言支持**: 支持德语界面

## 技术栈

- **语言**: Kotlin
- **UI框架**: Android XML布局
- **数据库**: Room (SQLite)
- **条形码扫描**: ZXing
- **相机**: CameraX
- **架构**: MVVM模式

## 项目结构

```
app/src/main/java/com/example/abandonscanner/
├── MainActivity.kt              # 主界面
├── EntryScanActivity.kt         # 录入扫描界面
├── CheckScanActivity.kt         # 检查扫描界面
├── QRCodeAdapter.kt             # 列表适配器
└── data/
    ├── AppDatabase.kt           # 数据库配置
    ├── QRCode.kt                # 数据实体
    └── QRCodeDao.kt             # 数据访问对象
```

## 主要功能

### 主界面 (MainActivity)
- 显示废弃条形码列表
- 提供功能按钮：
  - 录入废弃条形码
  - 检查条形码
  - 从文件导入
  - 清除所有记录

### 录入扫描 (EntryScanActivity)
- 使用ZXing扫描条形码
- 将扫描结果添加到废弃列表
- 支持连续扫描多个条形码

### 检查扫描 (CheckScanActivity)
- 扫描条形码并检查是否在废弃列表中
- 如果检测到废弃条形码，显示红色警告对话框
- 提供继续扫描或返回主界面的选项

## 构建和运行

1. 确保已安装Android Studio和Android SDK
2. 克隆项目到本地
3. 在Android Studio中打开项目
4. 连接Android设备或启动模拟器
5. 点击运行按钮构建并安装应用

## 依赖项

主要依赖项包括：
- ZXing for barcode scanning
- Room for local database
- CameraX for camera functionality
- AndroidX components

## 许可证

此项目仅供学习和个人使用。 