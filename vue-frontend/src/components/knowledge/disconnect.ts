import { Close, Connection, Switch } from "@element-plus/icons-vue";
import type { DocStep, LauncherDoc, LauncherDocs } from "./crash";

export const disconnectCategories = [
  {
    id: 'disconnect_decoder',
    name: 'DecoderException',
    icon: Close,
    description: '客机端发生异常',
    color: '#409EFF',
    launcherDocs: {
      pcl2: {
        docSteps: [
          { title: "放置文件", description: "放置配置文件", icon: "Document", jsonPath: "/docs/disconnect_put_file.json" },
          { title: "填写参数", description: "填写JVM参数", icon: "Document", jsonPath: "/docs/disconnect_pcl2_args.json" },
          { title: "获取文件", description: "获取Debug文件", icon: "InfoFilled", jsonPath: "/docs/disconnect_get_file.json" },
          { title: "补充信息", description: "补充信息", icon: "Loading", jsonPath: "/docs/crash_info.json" }
        ]
      },
      hmcl: {
        docSteps: [
          { title: "放置文件", description: "放置配置文件", icon: "Document", jsonPath: "/docs/disconnect_put_file.json" },
          { title: "填写参数", description: "填写JVM参数", icon: "Document", jsonPath: "/docs/disconnect_hmcl_args.json" },
          { title: "获取文件", description: "获取Debug文件", icon: "InfoFilled", jsonPath: "/docs/disconnect_get_file.json" },
          { title: "补充信息", description: "补充信息", icon: "Loading", jsonPath: "/docs/crash_info.json" }
        ]
      },
      fcl: {
        docSteps: [
          { title: "放置文件", description: "放置配置文件", icon: "Document", jsonPath: "/docs/disconnect_put_file.json" },
          { title: "填写参数", description: "填写JVM参数", icon: "Document", jsonPath: "/docs/disconnect_fcl_args.json" },
          { title: "获取文件", description: "获取Debug文件", icon: "InfoFilled", jsonPath: "/docs/disconnect_get_file.json" },
          { title: "补充信息", description: "补充信息", icon: "Loading", jsonPath: "/docs/crash_info.json" }
        ]
      }
    }
  },
  {
    id: 'disconnect_encoder',
    name: 'EncoderException',
    icon: Connection,
    description: '主机端发生异常',
    color: '#409EFF',
    launcherDocs: {
      server: {
        docSteps: [
          { title: "放置文件", description: "放置配置文件", icon: "Document", jsonPath: "/docs/disconnect_put_file.json" },
          { title: "填写参数", description: "填写JVM参数", icon: "Document", jsonPath: "/docs/disconnect_server_args.json" },
          { title: "获取文件", description: "获取Debug文件", icon: "InfoFilled", jsonPath: "/docs/disconnect_get_file.json" },
          { title: "补充信息", description: "补充信息", icon: "Tools", jsonPath: "/docs/crash_info.json" }
        ]
      },
      pcl2: {
        docSteps: [
          { title: "放置文件", description: "放置配置文件", icon: "Document", jsonPath: "/docs/disconnect_put_file.json" },
          { title: "填写参数", description: "填写JVM参数", icon: "Document", jsonPath: "/docs/disconnect_pcl2_args.json" },
          { title: "获取文件", description: "获取Debug文件", icon: "InfoFilled", jsonPath: "/docs/disconnect_get_file.json" },
          { title: "补充信息", description: "补充信息", icon: "Tools", jsonPath: "/docs/crash_info.json" }
        ]
      },
      hmcl: {
        docSteps: [
          { title: "放置文件", description: "放置配置文件", icon: "Document", jsonPath: "/docs/disconnect_put_file.json" },
          { title: "填写参数", description: "填写JVM参数", icon: "Document", jsonPath: "/docs/disconnect_hmcl_args.json" },
          { title: "获取文件", description: "获取Debug文件", icon: "InfoFilled", jsonPath: "/docs/disconnect_get_file.json" },
          { title: "补充信息", description: "补充信息", icon: "Tools", jsonPath: "/docs/crash_info.json" }
        ]
      },
      fcl: {
        docSteps: [
          { title: "放置文件", description: "放置配置文件", icon: "Document", jsonPath: "/docs/disconnect_put_file.json" },
          { title: "填写参数", description: "填写JVM参数", icon: "Document", jsonPath: "/docs/disconnect_fcl_args.json" },
          { title: "获取文件", description: "获取Debug文件", icon: "InfoFilled", jsonPath: "/docs/disconnect_get_file.json" },
          { title: "补充信息", description: "补充信息", icon: "Tools", jsonPath: "/docs/crash_info.json" }
        ]
      }
    }
  },
  {
    id: 'disconnect_common',
    name: '单纯网络问题类型',
    icon: Connection,
    description: '单纯网络问题类型',
    color: '#409EFF',
    launcherDocs: {
      server: {
        docSteps: [
          { title: "网络问题", description: "服务端网络问题的表现", icon: "Document", jsonPath: "/docs/disconnect_refused_server_desc.json" },
          { title: "补充信息", description: "补充信息", icon: "Document", jsonPath: "/docs/crash_info.json" }
        ]
      }
    }
  }
]