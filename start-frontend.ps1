# 启动前端（使用 Node 20.20.2）
# Node 20 路径: %USERPROFILE%\node-v20\node-v20.20.2-win-x64
$env:PATH = "$env:USERPROFILE\node-v20\node-v20.20.2-win-x64;$env:PATH"
Set-Location "frontend"
Write-Output "Node version: $((node --version))"
Write-Output "Starting dev server on http://localhost:3000"
npm run dev
