import { defineStore } from 'pinia'

interface UserInfo {
  id: number
  username: string
  role: string
  employeeName: string
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: (localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')!) : null) as UserInfo | null
  }),
  getters: {
    isLoggedIn: state => !!state.token,
    role: state => state.user?.role || ''
  },
  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUser(user: UserInfo) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
