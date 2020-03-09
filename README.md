## 简介
Spring Boot API 是一个基于Spring Boot & MyBatis plus的种子项目，用于快速构建中小型API项目，特点稳定、简单、快速，摆脱那些重复劳动

## 特征&提供
- 统一响应结果封装及生成工具
- 统一异常处理
- 简单的jwt认证
- 使用Druid Spring Boot Starter 集成Druid数据库连接池与监控, 地址为ip:port/druid/index.html
- 集成MyBatis plus，实现单表业务零SQL
- 支持多数据源，自由切换，只需方法或类上用 @DS 切换数据源
- 集成Swagger2,自动生成接口文档 地址为ip:port/swagger-ui.html
- 提供代码生成器，生成controller,service,serviceImpl,dao,mapper.xml
 
## 快速开始
1. 克隆项目
2. 导入```test```包里的mysql脚本user.sql
3. 对```test```包内的代码生成器```CodeGenerator```进行配置，主要是JDBC，因为要根据表名来生成代码
4. 输入表名，运行```CodeGenerator.main()```方法，生成基础代码（可能需要刷新项目目录才会出来）
5. 根据业务在基础代码上进行扩展
6. 对开发环境配置文件```application-dev.yml```进行配置，启动项目，Have Fun！
7. post调用接口ip:8080/api/user/login,参数json: {"username":"admin","password":"123456"},调用成功后, 返回token。
8. 以后调用api接口，header中传token，奔放！！！

## License
纯粹开源分享，感谢大家 [Star](https://github.com/aitangbao/springboot-api) 的支持。