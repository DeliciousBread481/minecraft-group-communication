# 游戏内崩溃解决方案

## 常见原因

1. **资源包冲突**：使用了不兼容的资源包
2. **世界生成错误**：生成区块时出现错误
3. **内存溢出**：游戏运行时内存不足
4. **MOD脚本错误**：MOD执行脚本时出现问题

## 解决步骤

### 1. 检查崩溃报告

崩溃报告通常位于以下位置：
- Windows: `%appdata%\.minecraft\crash-reports`
- Mac: `~/Library/Application Support/minecraft/crash-reports`
- Linux: `~/.minecraft/crash-reports`

### 2. 排除资源包问题

1. 移除所有资源包
2. 逐个添加资源包，确定是否有特定资源包导致崩溃

### 3. 增加内存分配

1. 打开启动器设置
2. 找到JVM参数或内存设置
3. 将内存设置提高到6GB（6144MB）或更多

### 4. 检查MOD冲突

特别注意这些可能导致游戏内崩溃的MOD类型：
- 世界生成类MOD
- 实体AI修改类MOD
- 大型技术类MOD

## 高级排查方法

### 使用JVM参数优化

添加以下JVM参数可能有助于减少崩溃：
```
-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1
```

### 尝试不同的Java版本

有时切换到特定版本的Java可以解决兼容性问题：
- 对于Minecraft 1.18+，尝试Java 17
- 对于Minecraft 1.17，尝试Java 16
- 对于旧版本，尝试特定版本的Java 8（如8u51）

如果问题仍然存在，请提供完整的崩溃报告以获取更具体的帮助。
