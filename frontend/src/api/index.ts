import request from '../utils/request'

// Auth
export const login = (data: any) => request.post('/auth/login', data)
export const getCurrentUser = () => request.get('/auth/currentUser')
export const logout = () => request.post('/auth/logout')

// Dept
export const getDeptTree = () => request.get('/dept/tree')
export const createDept = (data: any) => request.post('/dept', data)
export const updateDept = (data: any) => request.put('/dept', data)
export const deleteDept = (id: number) => request.delete(`/dept/${id}`)

// Position
export const getPositionList = () => request.get('/position')
export const createPosition = (data: any) => request.post('/position', data)
export const updatePosition = (data: any) => request.put('/position', data)
export const deletePosition = (id: number) => request.delete(`/position/${id}`)

// Employee
export const getEmployeeList = (params: any) => request.get('/employee', { params })
export const getEmployeeById = (id: number) => request.get(`/employee/${id}`)
export const createEmployee = (data: any) => request.post('/employee', data)
export const updateEmployee = (data: any) => request.put('/employee', data)
export const deleteEmployee = (id: number) => request.delete(`/employee/${id}`)
export const quitEmployee = (id: number) => request.put(`/employee/${id}/quit`)

// Attendance
export const clockIn = (data: any) => request.post('/attendance/clock', data)
export const getAttendanceRecords = (params: any) => request.get('/attendance/records', { params })
export const getTodayAttendance = () => request.get('/attendance/today')

// Leave
export const applyLeave = (data: any) => request.post('/leave/apply', data)
export const getMyLeaves = () => request.get('/leave/my')
export const getPendingLeaves = () => request.get('/leave/pending')
export const approveLeave = (data: any) => request.put('/leave/approve', data)

// Salary
export const getSalaryList = (params: any) => request.get('/salary', { params })
export const createSalary = (data: any) => request.post('/salary', data)
export const updateSalary = (data: any) => request.put('/salary', data)

// Dashboard
export const getDashboard = (params: any) => request.get('/dashboard', { params })
