import { Close, Connection, Switch } from "@element-plus/icons-vue";

export const disconnectCategories = [
  {
    id: 'disconnect_reset',
    name: 'Connection reset',
    icon: Switch,
    description: '连接重置',
    color: '#409EFF',
    //docPath:
  },
  {
    id: 'disconnect_encoder',
    name: 'EncoderException',
    icon: Connection,
    description: '主机端发生异常',
    color: '#409EFF',
    //docPath:
  },
  {
    id: 'disconnect_decoder',
    name: 'DecoderException',
    icon: Close,
    description: '客机端发生异常',
    color: '#409EFF',
    //docPath: '/docs/disconnect_kicked.md'
  },
  {
    id: 'disconnect_refused',
    name: 'Connection refused',
    icon: Connection,
    description: '连接被拒绝',
    color: '#409EFF',
    //docPath: '/docs/disconnect_lan.md'
  }
]
