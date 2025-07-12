import axios, { type AxiosInstance } from 'axios'

/**
 * 创建并配置Axios实例
 */
const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * 请求拦截器
 */
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
api.interceptors.response.use(
  response => {
    // 返回响应数据主体
    return response.data
  },
  error => {
    console.error('API Error:', error.response)
    return Promise.reject(error)
  }
)

export default api
