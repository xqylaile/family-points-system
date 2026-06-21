# 项目启动指南

## 前置准备

### 1. 安装 JDK 1.8
- 下载地址：https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html
- 配置环境变量 JAVA_HOME

### 2. 安装 Maven
- 下载地址：https://maven.apache.org/download.cgi
- 配置环境变量 MAVEN_HOME

### 3. 安装 MySQL 8.0
- 下载地址：https://dev.mysql.com/downloads/mysql/
- 安装后启动 MySQL 服务

## 快速启动步骤

### 步骤1：创建数据库

打开 MySQL 命令行或可视化工具（如 Navicat），执行：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS family_points DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 步骤2：导入数据

在 MySQL 中执行初始化脚本：

**方式一：命令行导入**
```bash
cd D:\projectDemo\family-points-system
mysql -u root -p family_points < src\main\resources\sql\init.sql
```

**方式二：可视化工具导入**
1. 打开 Navicat 等工具
2. 连接到 family_points 数据库
3. 选择"运行 SQL 文件"
4. 选择 `src\main\resources\sql\init.sql` 文件并执行

### 步骤3：修改数据库配置

编辑 `src\main\resources\application.yml` 文件，修改数据库密码：

```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码  # 修改这里
```

### 步骤4：启动项目

**方式一：使用 Maven 命令启动**
```bash
cd D:\projectDemo\family-points-system
mvn spring-boot:run
```

**方式二：使用 IDE 启动（推荐）**
1. 使用 IDEA 打开项目
2. 等待 Maven 依赖下载完成
3. 找到 `FamilyPointsApplication.java` 文件
4. 右键选择 "Run 'FamilyPointsApplication'"

### 步骤5：访问系统

启动成功后，在浏览器中访问：

```
http://localhost:8080
```

看到首页说明启动成功！

## 验证功能

### 1. 查看示例数据

系统已预置以下示例数据：

**成员列表：**
- 爸爸（家长）
- 妈妈（家长）
- 小明（小朋友）

**积分规则：**
- 学习类规则（如：完成作业、考试满分）
- 家务类规则（如：帮忙做家务）
- 行为习惯类规则（如：按时起床、乱扔玩具）

**奖品列表：**
- 玩具小汽车（50积分）
- 零食礼包（30积分）
- 看电视1小时（20积分）
- 现金10元（10积分）
- 游乐场门票（100积分）

### 2. 测试功能流程

1. **访问首页** → 查看各功能模块
2. **成员管理** → 查看预置成员信息
3. **积分变更** → 为小明添加积分（选择"完成作业"规则）
4. **积分兑换** → 用积分兑换奖品
5. **统计报表** → 查看积分统计数据

## 常见问题解决

### 问题1：Maven 依赖下载失败

**解决方案：**
配置国内 Maven 镜像，编辑 `~/.m2/settings.xml`：

```xml
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Aliyun Maven</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

### 问题2：数据库连接失败

**错误信息：** `Communications link failure`

**解决方案：**
1. 检查 MySQL 服务是否启动
2. 检查数据库用户名密码是否正确
3. 检查数据库名称是否为 `family_points`
4. 检查 MySQL 端口是否为 3306

### 问题3：端口被占用

**错误信息：** `Port 8080 was already in use`

**解决方案：**
修改 `application.yml` 中的端口：
```yaml
server:
  port: 8081  # 改为其他端口
```

### 问题4：页面样式异常

**解决方案：**
1. 清空浏览器缓存
2. 强制刷新页面（Ctrl + F5）
3. 检查 jQuery CDN 是否可访问

### 问题5：文件上传失败

**解决方案：**
在项目根目录手动创建 `upload` 文件夹：
```bash
cd D:\projectDemo\family-points-system
mkdir upload
mkdir upload\avatar
mkdir upload\record
mkdir upload\reward
```

## 开发环境推荐

### IDE
- IntelliJ IDEA 2020+ （推荐）
- Eclipse 2020+

### 数据库工具
- Navicat for MySQL（推荐）
- MySQL Workbench
- DBeaver

### 浏览器
- Chrome（推荐）
- Edge
- Firefox

## 目录结构说明

```
family-points-system/
├── src/main/
│   ├── java/             # Java 源代码
│   ├── resources/
│   │   ├── sql/          # 数据库脚本
│   │   ├── static/       # 静态资源（CSS、JS）
│   │   ├── templates/    # 页面模板
│   │   └── application.yml  # 配置文件
├── upload/               # 文件上传目录（需手动创建）
├── pom.xml              # Maven 依赖配置
└── README.md            # 项目说明文档
```

## 下一步

项目启动成功后，你可以：

1. **体验功能** - 按照测试流程体验各个功能模块
2. **查看代码** - 了解项目结构和实现方式
3. **自定义开发** - 根据需求添加新功能
4. **数据备份** - 定期备份 MySQL 数据库

## 技术支持

如遇到其他问题，请查看：
- README.md - 项目详细说明
- 项目源代码注释
- Spring Boot 官方文档

祝使用愉快！
