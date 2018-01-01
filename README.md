# S001-CMS-JAVA
CMS系统微服务，SpringBoot框架

## build & run
mvn clean package docker:build -DskipTests
docker run --env PROFILE=test  -p 127.0.0.1:10002:10002 --name zuul registry.cn-qingdao.aliyuncs.com/ac109/zuul:1.2


## version
1.2
- 登录统一认证

