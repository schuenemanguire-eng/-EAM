# 启动后端（确保MySQL已启动，且已执行schema.sql初始化数据库）
# 如需修改数据库密码，请先编辑 backend/src/main/resources/application.yml
cd backend
& "D:\CodeSources\apache-maven-3.9.4\bin\mvn.cmd" clean package -DskipTests -s "D:\CodeSources\apache-maven-3.9.4\conf\settings.xml"
java -jar target\enterprise-employee-management-1.0.0.jar
