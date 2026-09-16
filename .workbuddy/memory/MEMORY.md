# 苍穹外卖（sky-take-out）项目常识

## 模块结构
- `sky-common`：常量、异常、工具、属性、Result/PageResult
- `sky-pojo`：entity / dto / vo
- `sky-server`：controller（分 admin / user 两套）、service、mapper、config、interceptor、task、websocket
- 分层约定：Mapper 简单 SQL 用注解，动态 SQL 写 `resources/mapper/*.xml`
  （`mapper-locations: classpath:mapper/*.xml`，`type-aliases-package: com.sky.entity`）

## 本机构建与运行（重要）
- JDK：`C:\Users\wzy30\.jdks\ms-17.0.20`（IDEA 项目 SDK 为 ms-17；系统默认 java 是 25，直接构建会有兼容问题）
- Maven：`D:\develop\apache-maven-3.9.11`，**本地仓库是 `D:\develop\apache-maven-3.9.11\mvn_repo`**（由 conf/settings.xml 指定，不是 `~/.m2`）
- **`mvn` 批处理/脚本在本机 Git Bash 下不可用**（报 `ClassNotFoundException: plexus-classworlds.launcher.Launcher`），
  可靠做法是直接调 launcher：
  ```bash
  export JAVA_HOME=/c/Users/wzy30/.jdks/ms-17.0.20
  MVN=/d/develop/apache-maven-3.9.11
  "$JAVA_HOME/bin/java" -classpath "$(cygpath -w $MVN/boot/plexus-classworlds-2.9.0.jar)" \
    -Dclassworlds.conf="$(cygpath -w $MVN/bin/m2.conf)" -Dmaven.home="$(cygpath -w $MVN)" \
    -Dmaven.multiModuleProjectDirectory="$(cygpath -w $PWD)" \
    org.codehaus.plexus.classworlds.launcher.Launcher -o -DskipTests clean package
  ```
- 运行时环境：MySQL 8/9 已装在 `D:\mysql-9.4.0-winx64`（root/1234，库 `sky_take_out`）；Redis 通常**未启动**，
  需测试购物车之外依赖 Redis 的功能（店铺状态）时要先起 Redis
- 应用默认端口 8080，本机 8080 常被占用，验证时可临时 `--server.port=8090`

## 易踩的坑
- **同名 Controller 必须显式指定 Bean 名**：`admin` 与 `user` 下同名类（Dish/Setmeal/Category/Shop/Order 等）
  需写 `@RestController("userXxxController")`，否则启动报 ConflictingBeanDefinitionException
- 修改 `resources` 下文件名后务必 `clean`：`target/classes` 残留旧文件会让 MyBatis 报
  “Mapped Statements collection already contains value for ...”
- 用户端 JWT 请求头名是 `authentication`，管理端是 `token`；密钥分别为 `itheima` / `itcast`
- 端到端验证用户端接口可手工签发令牌（依赖 jjwt 0.9.1 + jaxb-api，缺 jaxb 会报 `NoClassDefFoundError: javax/xml/bind/DatatypeConverter`）
- 菜品 id 从 46 起（非 1），写测试脚本时不要假设自增从 1 开始

## 业务约定
- 订单状态：1待付款 2待接单 3已接单 4派送中 5已完成 6已取消；支付状态：0未支付 1已支付 2退款
- 微信支付未配置商户信息，`OrderServiceImpl.payment()` 采用模拟支付回调；接入真实支付需替换为 `WeChatPayUtil`
