# 家庭积分系统

一个基于 Spring Boot 的家庭积分管理系统，用于家庭成员的积分管理、规则设置、奖品兑换等功能。

## 功能特性

### 1. 家庭成员管理
- 成员信息录入（姓名、角色、年龄、头像）
- 成员类型区分（家长/小朋友）
- 积分余额和累计统计
- 积分排行榜展示

### 2. 积分规则管理
- 加分/扣分规则设置
- 规则分类管理（学习类、家务类、行为习惯类等）
- 按成员类型设置适用规则
- 规则使用统计

### 3. 积分变更管理
- 积分加减操作
- 根据成员类型自动过滤规则
- 上传凭证图片
- 变更记录查询和撤销

### 4. 奖品池管理
- 奖品信息管理
- 库存管理
- 上架/下架控制
- 奖品分类

### 5. 积分兑换
- 积分兑换奖品
- 自动扣减积分和库存
- 兑换状态管理
- 撤销兑换功能

### 6. 统计报表
- 成员积分排行
- 热门奖品排行
- 积分变更趋势
- 兑换记录统计

### 7. 系统管理
- 系统配置管理
- 积分金额比例设置
- 系统公告设置

## 技术栈

- **后端框架**: Spring Boot 2.7.18
- **持久层**: MyBatis Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **模板引擎**: Thymeleaf
- **工具类**: Hutool 5.8.16
- **前端**: HTML5 + CSS3 + jQuery

## 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

## 快速开始

### 1. 数据库初始化

执行数据库初始化脚本：

```bash
# 创建数据库并导入数据
mysql -u root -p < src/main/resources/sql/init.sql
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/family_points?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 3. 启动项目

```bash
# 使用 Maven 启动
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/family-points-system-1.0.0.jar
```

### 4. 访问系统

浏览器访问：http://localhost:8080

## 项目结构

```
family-points-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/family/points/
│   │   │       ├── FamilyPointsApplication.java  # 启动类
│   │   │       ├── common/                       # 公共类
│   │   │       ├── config/                       # 配置类
│   │   │       ├── controller/                   # 控制器
│   │   │       ├── entity/                       # 实体类
│   │   │       ├── mapper/                       # Mapper接口
│   │   │       ├── service/                      # 服务接口
│   │   │       │   └── impl/                     # 服务实现
│   │   │       └── util/                         # 工具类
│   │   └── resources/
│   │       ├── sql/                              # SQL脚本
│   │       ├── static/                           # 静态资源
│   │       │   ├── css/
│   │       │   └── js/
│   │       ├── templates/                        # 页面模板
│   │       │   ├── member/                       # 成员管理
│   │       │   ├── rule/                         # 规则管理
│   │       │   ├── record/                       # 积分记录
│   │       │   ├── reward/                       # 奖品管理
│   │       │   ├── exchange/                     # 积分兑换
│   │       │   ├── statistics/                   # 统计报表
│   │       │   ├── system/                       # 系统管理
│   │       │   └── index.html                    # 首页
│   │       └── application.yml                   # 配置文件
│   └── test/                                     # 测试代码
├── upload/                                       # 文件上传目录
├── pom.xml                                       # Maven配置
└── README.md                                     # 说明文档
```

## 核心功能说明

### 积分换算规则
- 默认：1积分 = 1元人民币
- 可在系统设置中调整

### 成员类型
- **家长（PARENT）**: 适用家长专属规则和通用规则
- **小朋友（CHILD）**: 适用小朋友专属规则和通用规则

### 规则类型
- **加分规则（ADD）**: 完成任务或良好行为奖励积分
- **扣分规则（DEDUCT）**: 不良行为扣除积分

### 兑换状态
- **已兑换（EXCHANGED）**: 刚完成兑换
- **已发放（DELIVERED）**: 奖品已发放给成员
- **已完成（COMPLETED）**: 整个兑换流程完成

## 注意事项

1. **数据安全**: 建议定期备份数据库
2. **积分撤销**: 撤销积分记录会恢复成员积分
3. **兑换撤销**: 撤销兑换会恢复积分和库存
4. **文件上传**: 上传的图片保存在 `upload/` 目录
5. **库存管理**: 兑换时会自动扣减库存，库存不足时无法兑换

## 默认数据

系统初始化后包含示例数据：
- 3个家庭成员（爸爸、妈妈、小明）
- 11条积分规则
- 5个示例奖品

## 开发说明

### 添加新功能模块

1. 创建实体类（Entity）
2. 创建 Mapper 接口
3. 创建 Service 接口和实现类
4. 创建 Controller
5. 创建页面模板
6. 更新首页导航

### 自定义样式

修改 `src/main/resources/static/css/style.css` 文件

## 常见问题

### 1. 数据库连接失败
检查 MySQL 服务是否启动，确认用户名密码是否正确

### 2. 文件上传失败
检查 `upload/` 目录是否存在且有写入权限

### 3. 页面显示异常
清空浏览器缓存后重试

## 版本历史

- v1.0.0 (2026-06-20)
  - 初始版本发布
  - 完成核心功能开发

## 开源协议

MIT License

## 联系方式

如有问题或建议，欢迎提交 Issue。
