import { CircleCloseFilled, Close, WarningFilled } from '@element-plus/icons-vue'

// 文档步骤类型定义
export interface DocStep {
  title: string;
  description: string;
  icon: string;
  docSuffix: string;
}

// 崩溃类别数据
export const crashCategories = [
  {
    id: 'crash_startup',
    name: '启动失败',
    icon: Close,
    description: '游戏无法启动或启动过程中崩溃',
    color: '#9c27b0',
    docPath: '/docs/crash_startup.md',
    // 自定义文档步骤
    docSteps: [
      {
        title: "问题描述",
        description: "启动失败的现象",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "常见原因",
        description: "启动失败的常见原因分析",
        icon: "InfoFilled",
        docSuffix: "_cause"
      },
      {
        title: "解决方法",
        description: "可行的解决步骤",
        icon: "Loading",
        docSuffix: "_solution"
      },
      {
        title: "日志分析",
        description: "如何分析日志找出问题",
        icon: "Search",
        docSuffix: "_logs"
      }
    ]
  },
  {
    id: 'crash_suddenly',
    name: '游戏闪退',
    icon: WarningFilled,
    description: '游戏游玩中突然崩溃且窗口突然消失',
    color: '#9c27b0',
    docPath: '/docs/crash_mod.md',
    // 闪退类型文档步骤
    docSteps: [
      {
        title: "问题表现",
        description: "闪退的具体表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "模组冲突",
        description: "检查模组冲突问题",
        icon: "Warning",
        docSuffix: "_mods"
      },
      {
        title: "性能问题",
        description: "检查是否为性能问题",
        icon: "Odometer",
        docSuffix: "_performance"
      },
      {
        title: "解决方案",
        description: "解决闪退问题的方法",
        icon: "SuccessFilled",
        docSuffix: "_solution"
      }
    ]
  },
  {
    id: 'crash_ingame',
    name: '游戏内崩溃',
    icon: CircleCloseFilled,
    description: '游戏运行中突然崩溃',
    color: '#9c27b0',
    docPath: '/docs/crash_ingame.md',
    // 游戏内崩溃文档步骤
    docSteps: [
      {
        title: "崩溃现象",
        description: "游戏内崩溃的现象",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "错误信息",
        description: "分析崩溃时的错误信息",
        icon: "Warning",
        docSuffix: "_errors"
      },
      {
        title: "解决方案",
        description: "修复游戏内崩溃的方法",
        icon: "Tools",
        docSuffix: "_solution"
      }
    ]
  }
]
