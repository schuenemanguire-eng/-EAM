import axios from 'axios'
import { message } from 'ant-design-vue'

const request = axios.create({
  // CloudStudio/Vite 代理模式下使用相对路径 /api（由 vite proxy 转发到后端）
  // 生产部署时可通过 VITE_API_BASE_URL 环境变量指定后端地址
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code && res.code !== 200) {
      message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    } else {
      message.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
