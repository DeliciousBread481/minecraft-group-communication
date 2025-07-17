import { CircleCloseFilled, QuestionFilled, WarningFilled } from "@element-plus/icons-vue";

export const lauchercategories = [
  {
    id: 'launcher_failedstart',
    name: '启动器点不开',
    icon: CircleCloseFilled,
    description: '启动器无法打开',
    color: '#E6A23C',
    docPath: '/docs/launcher_crash.md',
    // 启动器无法打开的文档步骤
    docSteps: [
      {
        title: "问题表现",
        description: "启动器无法打开的现象",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "系统检查",
        description: "检查系统环境",
        icon: "Monitor",
        docSuffix: "_system"
      },
      {
        title: "重装方法",
        description: "正确的重装步骤",
        icon: "Refresh",
        docSuffix: "_reinstall"
      },
      {
        title: "权限问题",
        description: "处理权限问题",
        icon: "Lock",
        docSuffix: "_permission"
      }
    ]
  },
  {
    id: 'launcher_download',
    name: '下载问题',
    icon: WarningFilled,
    description: '游戏文件或资源下载失败',
    color: '#E6A23C',
    docPath: '/docs/launcher_download.md',
    // 下载问题的文档步骤
    docSteps: [
      {
        title: "下载失败",
        description: "下载失败的表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "网络诊断",
        description: "网络问题诊断",
        icon: "Connection",
        docSuffix: "_network"
      },
      {
        title: "代理设置",
        description: "配置下载代理",
        icon: "Setting",
        docSuffix: "_proxy"
      },
      {
        title: "解决方法",
        description: "解决下载问题的方法",
        icon: "SuccessFilled",
        docSuffix: "_solution"
      }
    ]
  },
  {
    id: 'launcher_login',
    name: '账号登录问题',
    icon: QuestionFilled,
    description: '无法登录账号或认证失败',
    color: '#E6A23C',
    docPath: '/docs/launcher_login.md',
    // 账号登录问题的文档步骤
    docSteps: [
      {
        title: "登录失败",
        description: "登录失败的表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "账号检查",
        description: "账号状态检查",
        icon: "User",
        docSuffix: "_account"
      },
      {
        title: "认证服务",
        description: "认证服务状态",
        icon: "Service",
        docSuffix: "_auth"
      },
      {
        title: "解决方法",
        description: "修复登录问题的方法",
        icon: "Key",
        docSuffix: "_solution"
      }
    ]
  }
]
