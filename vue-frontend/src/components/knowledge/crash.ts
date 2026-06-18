import { CircleCloseFilled, Close, WarningFilled } from '@element-plus/icons-vue'

// 文档步骤类型定义
export interface DocStep {
  title: string;
  description: string;
  icon: string;
  docSuffix?: string;
  jsonPath?: string;
}

export interface LauncherDoc {
  docPath?: string;
  docSteps: DocStep[];
}

export interface LauncherDocs {
  pcl2: LauncherDoc;
  hmcl: LauncherDoc;
  fcl: LauncherDoc;
  server: LauncherDoc;
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
        docSteps: [
          { title: "导出错误报告", description: "在 PCL2 中导出启动错误报告", icon: "Document", jsonPath: "/docs/crash_startup_pcl2_export.json" },
          { title: "补充更多信息", description: "提供更多问题相关信息", icon: "InfoFilled", jsonPath: "/docs/crash_startup_info.json" }
        ]
      },
      hmcl: {
        docSteps: [
          { title: "导出错误报告", description: "在 HMCL 中导出启动错误报告", icon: "Document", jsonPath: "/docs/crash_startup_hmcl_export.json" },
          { title: "补充更多信息", description: "提供更多问题相关信息", icon: "InfoFilled", jsonPath: "/docs/crash_startup_info.json" }
        ]
      },
      fcl: {
        docSteps: [
          { title: "导出错误报告", description: "在 FCL 中导出启动错误报告", icon: "Document", jsonPath: "/docs/crash_startup_fcl_export.json" },
          { title: "补充更多信息", description: "提供更多问题相关信息", icon: "InfoFilled", jsonPath: "/docs/crash_startup_info.json" }
        ]
      },
      server: {
        docSteps: [
          { title: "导出错误报告", description: "导出服务端启动错误报告", icon: "Document", jsonPath: "/docs/crash_startup_server_export.json" },
          { title: "补充更多信息", description: "提供更多问题相关信息", icon: "InfoFilled", jsonPath: "/docs/crash_startup_info.json" }
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
    launcherDocs: {
      pcl2: {
        docSteps: [
          { title: "导出错误报告", description: "在 PCL2 中导出启动错误报告", icon: "Document", jsonPath: "/docs/crash_startup_pcl2_export.json" },
          { title: "补充更多信息", description: "提供更多问题相关信息", icon: "InfoFilled", jsonPath: "/docs/crash_info.json" }
        ]
      },
      hmcl: {
        docSteps: [
          { title: "导出错误报告", description: "在 HMCL 中导出启动错误报告", icon: "Document", jsonPath: "/docs/crash_startup_hmcl_export.json" },
          { title: "补充更多信息", description: "提供更多问题相关信息", icon: "InfoFilled", jsonPath: "/docs/crash_info.json" }
        ]
      },
      fcl: {
        docSteps: [
          { title: "导出错误报告", description: "在 FCL 中导出启动错误报告", icon: "Document", jsonPath: "/docs/crash_startup_fcl_export.json" },
          { title: "补充更多信息", description: "提供更多问题相关信息", icon: "InfoFilled", jsonPath: "/docs/crash_info.json" }
        ]
      },
      server: {
        docSteps: [
          { title: "导出错误报告", description: "导出服务端崩溃错误报告", icon: "Document", jsonPath: "/docs/crash_ingame_server_export.json" },
          { title: "补充更多信息", description: "提供更多问题相关信息", icon: "InfoFilled", jsonPath: "/docs/crash_info.json" }
        ]
      }
    }
  }
]