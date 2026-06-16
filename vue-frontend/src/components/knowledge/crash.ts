import { CircleCloseFilled, Close, WarningFilled } from '@element-plus/icons-vue'

// 文档步骤类型定义
export interface DocStep {
  title: string;
  description: string;
  icon: string;
  docSuffix: string;
}

export interface LauncherDoc {
  docPath: string;
  docSteps: DocStep[];
}

export interface LauncherDocs {
  pcl2: LauncherDoc;
  hmcl: LauncherDoc;
  fcl: LauncherDoc;
}

// 崩溃类别数据
export const crashCategories = [
  {
    id: 'crash_startup',
    name: '启动失败',
    icon: Close,
    description: '游戏无法启动或启动过程中崩溃',
    color: '#9c27b0',
    launcherDocs: {  
      pcl2: {  
        docPath: '/docs/crash_startup_pcl2.md',  
        docSteps: [  
          { title: "问题描述", description: "启动失败的现象", icon: "Document", docSuffix: "_desc" },  
          { title: "常见原因", description: "启动失败的常见原因分析", icon: "InfoFilled", docSuffix: "_cause" },  
          { title: "解决方法", description: "可行的解决步骤", icon: "Loading", docSuffix: "_solution" },  
          { title: "日志分析", description: "如何分析日志找出问题", icon: "Search", docSuffix: "_logs" }  
        ]  
      },  
      hmcl: {  
        docPath: '/docs/crash_startup_hmcl.md',  
        docSteps: [  
          { title: "问题描述", description: "启动失败的现象", icon: "Document", docSuffix: "_desc" },  
          { title: "常见原因", description: "启动失败的常见原因分析", icon: "InfoFilled", docSuffix: "_cause" },  
          { title: "解决方法", description: "可行的解决步骤", icon: "Loading", docSuffix: "_solution" },  
          { title: "日志分析", description: "如何分析日志找出问题", icon: "Search", docSuffix: "_logs" }  
        ]  
      },  
      fcl: {  
        docPath: '/docs/crash_startup_fcl.md',  
        docSteps: [  
          { title: "问题描述", description: "启动失败的现象", icon: "Document", docSuffix: "_desc" },  
          { title: "常见原因", description: "启动失败的常见原因分析", icon: "InfoFilled", docSuffix: "_cause" },  
          { title: "解决方法", description: "可行的解决步骤", icon: "Loading", docSuffix: "_solution" },  
          { title: "日志分析", description: "如何分析日志找出问题", icon: "Search", docSuffix: "_logs" }  
        ]  
      }  
    }
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
