import { Close, Connection, Switch } from "@element-plus/icons-vue";

export const disconnectCategories = [
  {
    id: 'disconnect_reset',
    name: 'Connection reset',
    icon: Switch,
    description: '连接重置',
    color: '#409EFF',
    docPath: '/docs/disconnect_reset.md',
    // 连接重置的文档步骤
    docSteps: [
      {
        title: "问题表现",
        description: "连接重置的具体表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "网络分析",
        description: "网络问题分析",
        icon: "Connection",
        docSuffix: "_network"
      },
      {
        title: "解决方案",
        description: "修复连接重置的方法",
        icon: "Tools",
        docSuffix: "_solution"
      }
    ]
  },
  {
    id: 'disconnect_encoder',
    name: 'EncoderException',
    icon: Connection,
    description: '主机端发生异常',
    color: '#409EFF',
    docPath: '/docs/disconnect_encoder.md',
    // 主机端异常的文档步骤
    docSteps: [
      {
        title: "问题描述",
        description: "主机端异常的表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "错误分析",
        description: "主机端异常的原因",
        icon: "InfoFilled",
        docSuffix: "_cause"
      },
      {
        title: "解决方法",
        description: "修复主机端异常的方法",
        icon: "Tools",
        docSuffix: "_solution"
      }
    ]
  },
  {
    id: 'disconnect_decoder',
    name: 'DecoderException',
    icon: Close,
    description: '客机端发生异常',
    color: '#409EFF',
    docPath: '/docs/disconnect_kicked.md',
    // 客机端异常的文档步骤
    docSteps: [
      {
        title: "异常表现",
        description: "客机端异常的表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "客户端分析",
        description: "客户端问题分析",
        icon: "InfoFilled",
        docSuffix: "_analysis"
      },
      {
        title: "解决步骤",
        description: "解决客机端异常的步骤",
        icon: "Loading",
        docSuffix: "_solution"
      }
    ]
  },
  {
    id: 'disconnect_refused',
    name: 'Connection refused',
    icon: Connection,
    description: '连接被拒绝',
    color: '#409EFF',
    docPath: '/docs/disconnect_lan.md',
    // 连接被拒绝的文档步骤
    docSteps: [
      {
        title: "拒绝现象",
        description: "连接被拒绝的表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "防火墙检查",
        description: "检查防火墙设置",
        icon: "Lock",
        docSuffix: "_firewall"
      },
      {
        title: "网络设置",
        description: "检查网络设置",
        icon: "Setting",
        docSuffix: "_network"
      },
      {
        title: "解决方案",
        description: "解决连接被拒绝的方法",
        icon: "SuccessFilled",
        docSuffix: "_solution"
      }
    ]
  }
]
