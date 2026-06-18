import { CircleCloseFilled, QuestionFilled, WarningFilled } from "@element-plus/icons-vue";
import type { DocStep, LauncherDoc, LauncherDocs } from "./crash";

export const lauchercategories = [
  {
    id: 'launcher_failedstart',
    name: '启动器点不开',
    icon: CircleCloseFilled,
    description: '启动器无法打开',
    color: '#E6A23C',
    launcherDocs: {
      pcl2: {
        docSteps: [
          { title: "问题表现", description: "启动器无法打开的现象", icon: "Document", jsonPath: "/docs/launcher_failedstart_pcl2_desc.json" },
          { title: "系统检查", description: "检查系统环境", icon: "Monitor", jsonPath: "/docs/launcher_failedstart_pcl2_system.json" },
          { title: "重装方法", description: "正确的重装步骤", icon: "Refresh", jsonPath: "/docs/launcher_failedstart_pcl2_reinstall.json" },
          { title: "权限问题", description: "处理权限问题", icon: "Lock", jsonPath: "/docs/launcher_failedstart_pcl2_permission.json" }
        ]
      },
      hmcl: {
        docSteps: [
          { title: "问题表现", description: "启动器无法打开的现象", icon: "Document", jsonPath: "/docs/launcher_failedstart_hmcl_desc.json" },
          { title: "系统检查", description: "检查系统环境", icon: "Monitor", jsonPath: "/docs/launcher_failedstart_hmcl_system.json" },
          { title: "重装方法", description: "正确的重装步骤", icon: "Refresh", jsonPath: "/docs/launcher_failedstart_hmcl_reinstall.json" },
          { title: "权限问题", description: "处理权限问题", icon: "Lock", jsonPath: "/docs/launcher_failedstart_hmcl_permission.json" }
        ]
      },
      fcl: {
        docSteps: [
          { title: "问题表现", description: "启动器无法打开的现象", icon: "Document", jsonPath: "/docs/launcher_failedstart_fcl_desc.json" },
          { title: "系统检查", description: "检查系统环境", icon: "Monitor", jsonPath: "/docs/launcher_failedstart_fcl_system.json" },
          { title: "重装方法", description: "正确的重装步骤", icon: "Refresh", jsonPath: "/docs/launcher_failedstart_fcl_reinstall.json" },
          { title: "权限问题", description: "处理权限问题", icon: "Lock", jsonPath: "/docs/launcher_failedstart_fcl_permission.json" }
        ]
      }
    }
  },
  {
    id: 'launcher_download',
    name: '下载问题',
    icon: WarningFilled,
    description: '游戏文件或资源下载失败',
    color: '#E6A23C',
    launcherDocs: {
      pcl2: {
        docSteps: [
          { title: "下载失败", description: "下载失败的表现", icon: "Document", jsonPath: "/docs/launcher_download_pcl2_desc.json" },
          { title: "网络诊断", description: "网络问题诊断", icon: "Connection", jsonPath: "/docs/launcher_download_pcl2_network.json" },
          { title: "代理设置", description: "配置下载代理", icon: "Setting", jsonPath: "/docs/launcher_download_pcl2_proxy.json" },
          { title: "解决方法", description: "解决下载问题的方法", icon: "SuccessFilled", jsonPath: "/docs/launcher_download_pcl2_solution.json" }
        ]
      },
      hmcl: {
        docSteps: [
          { title: "下载失败", description: "下载失败的表现", icon: "Document", jsonPath: "/docs/launcher_download_hmcl_desc.json" },
          { title: "网络诊断", description: "网络问题诊断", icon: "Connection", jsonPath: "/docs/launcher_download_hmcl_network.json" },
          { title: "代理设置", description: "配置下载代理", icon: "Setting", jsonPath: "/docs/launcher_download_hmcl_proxy.json" },
          { title: "解决方法", description: "解决下载问题的方法", icon: "SuccessFilled", jsonPath: "/docs/launcher_download_hmcl_solution.json" }
        ]
      },
      fcl: {
        docSteps: [
          { title: "下载失败", description: "下载失败的表现", icon: "Document", jsonPath: "/docs/launcher_download_fcl_desc.json" },
          { title: "网络诊断", description: "网络问题诊断", icon: "Connection", jsonPath: "/docs/launcher_download_fcl_network.json" },
          { title: "代理设置", description: "配置下载代理", icon: "Setting", jsonPath: "/docs/launcher_download_fcl_proxy.json" },
          { title: "解决方法", description: "解决下载问题的方法", icon: "SuccessFilled", jsonPath: "/docs/launcher_download_fcl_solution.json" }
        ]
      }
    }
  },
  {
    id: 'launcher_login',
    name: '账号登录问题',
    icon: QuestionFilled,
    description: '无法登录账号或认证失败',
    color: '#E6A23C',
    launcherDocs: {
      pcl2: {
        docSteps: [
          { title: "登录失败", description: "登录失败的表现", icon: "Document", jsonPath: "/docs/launcher_login_pcl2_desc.json" },
          { title: "账号检查", description: "账号状态检查", icon: "User", jsonPath: "/docs/launcher_login_pcl2_account.json" },
          { title: "认证服务", description: "认证服务状态", icon: "Service", jsonPath: "/docs/launcher_login_pcl2_auth.json" },
          { title: "解决方法", description: "修复登录问题的方法", icon: "Key", jsonPath: "/docs/launcher_login_pcl2_solution.json" }
        ]
      },
      hmcl: {
        docSteps: [
          { title: "登录失败", description: "登录失败的表现", icon: "Document", jsonPath: "/docs/launcher_login_hmcl_desc.json" },
          { title: "账号检查", description: "账号状态检查", icon: "User", jsonPath: "/docs/launcher_login_hmcl_account.json" },
          { title: "认证服务", description: "认证服务状态", icon: "Service", jsonPath: "/docs/launcher_login_hmcl_auth.json" },
          { title: "解决方法", description: "修复登录问题的方法", icon: "Key", jsonPath: "/docs/launcher_login_hmcl_solution.json" }
        ]
      },
      fcl: {
        docSteps: [
          { title: "登录失败", description: "登录失败的表现", icon: "Document", jsonPath: "/docs/launcher_login_fcl_desc.json" },
          { title: "账号检查", description: "账号状态检查", icon: "User", jsonPath: "/docs/launcher_login_fcl_account.json" },
          { title: "认证服务", description: "认证服务状态", icon: "Service", jsonPath: "/docs/launcher_login_fcl_auth.json" },
          { title: "解决方法", description: "修复登录问题的方法", icon: "Key", jsonPath: "/docs/launcher_login_fcl_solution.json" }
        ]
      }
    }
  }
]