# 启动崩溃解决方案

## 常见原因

1. **显卡驱动问题**：过旧或不兼容的显卡驱动
2. **Java版本不兼容**：使用了错误的Java版本
3. **内存分配不足**：分配给Minecraft的内存不足
4. **MOD冲突**：安装了不兼容的MOD

## 解决步骤

### 1. 更新显卡驱动

- NVIDIA显卡：访问 [NVIDIA官网](https://www.nvidia.com/Download/index.aspx) 下载最新驱动
- AMD显卡：访问 [AMD官网](https://www.amd.com/zh-hans/support) 下载最新驱动
- Intel集成显卡：访问 [Intel官网](https://www.intel.cn/content/www/cn/zh/download-center/home.html) 下载最新驱动

### 2. 检查Java版本

确保您安装了正确版本的Java：
- Minecraft 1.17及以上版本需要Java 16或更高版本
- Minecraft 1.12-1.16.5需要Java 8
- 可以在 [Java官网](https://www.java.com) 下载对应版本

### 3. 增加内存分配

1. 打开启动器设置
2. 找到JVM参数或内存设置
3. 将内存设置为至少2GB（2048MB），推荐4GB（4096MB）

### 4. 检查MOD兼容性

1. 尝试在没有任何MOD的情况下启动游戏
2. 如果成功，则逐个添加MOD直到找到问题MOD
3. 确保所有MOD与您的Minecraft版本兼容

## 常见错误代码

- **EXCEPTION_ACCESS_VIOLATION**：通常是由于显卡驱动问题
- **OutOfMemoryError**：内存分配不足
- **ClassNotFoundException**：MOD缺少依赖或版本不匹配

如果以上方法无法解决问题，请提供详细的崩溃报告以获取更具体的帮助。
