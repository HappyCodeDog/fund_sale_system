# Fund Sale System - DDD Refactoring

基金代销系统 - 使用领域驱动设计(DDD)重构的申购业务模块

## 项目概述

本项目是对原有C语言基金代销系统的Java重构，采用Spring Boot + MyBatis + Maven技术栈，并运用领域驱动设计(DDD)思想进行架构设计。

## 技术架构

### 技术栈
- **Java**: 11
- **Spring Boot**: 2.7.18
- **MyBatis**: 2.3.2
- **Oracle Database**: 21c
- **Maven**: 3.6+
- **Resilience4j**: 断路器模式
- **Micrometer**: 监控指标

### 架构模式
- **领域驱动设计(DDD)**: 战术模式(Entities, Value Objects, Aggregates, Domain Services, Repositories)
- **六边形架构**: 领域层在中心，适配器在边缘
- **Saga模式**: 处理分布式事务和补偿
- **策略模式**: 处理不同的记账场景
- **防腐层(ACL)**: 隔离外部系统集成

## 项目结构

```
fund-sale-system/
├── pom.xml                    # 父POM
├── fund-common/              # 共享组件
│   ├── exception/           # 异常定义
│   ├── domain/              # DDD基础接口
│   ├── integration/         # 外部系统接口
│   ├── money/               # Money值对象
│   └── utils/               # 工具类
├── fund-product/            # 产品限界上下文
│   ├── domain/              # 领域模型
│   └── infrastructure/      # 基础设施层
├── fund-customer/           # 客户限界上下文
│   ├── domain/              # 领域模型
│   └── infrastructure/      # 基础设施层
├── fund-marketing/          # 营销限界上下文
│   ├── domain/              # 领域模型
│   └── infrastructure/      # 基础设施层
├── fund-trading/            # 交易限界上下文(核心)
│   ├── domain/              # 领域模型
│   │   ├── model/          # 聚合根、实体、值对象
│   │   ├── service/        # 领域服务
│   │   └── repository/     # 仓储接口
│   ├── application/         # 应用服务
│   └── infrastructure/      # 基础设施层
└── fund-application/        # Spring Boot应用模块
    ├── api/                # REST API控制器
    └── resources/          # 配置文件
```

## 核心业务流程

### 基金申购流程

1. **请求解析**: 解析类FIX格式的交易请求
2. **多层校验**:
   - 产品信息校验(状态、渠道)
   - 账户与份额校验(客户信息、风险等级匹配)
   - 金额与额度校验(最小/最大金额、TA日累计额度)
3. **交易处理**(Saga模式):
   - 生成唯一流水号
   - 优惠券试算(可选)
   - 本地事务: 保存交易请求 + 创建份额记录(初始为0)
   - **Saga步骤1**: 调用营销系统用券
   - **Saga步骤2**: 调用核心系统记账(策略模式):
     * `ExchangeAndAccountingStrategy`: 外币兑换场景
     * `DirectAccountingStrategy`: 交易时间内记账
     * `FreezeStrategy`: 非交易时间冻结
   - 本地事务: 更新状态
4. **补偿处理**:
   - 追踪Saga状态
   - 失败时反向执行补偿操作:
     * 记账失败 → 异步还券
     * 冻结失败 → 无需补偿
   - 重试机制(指数退避)
   - 死信队列(人工介入)
5. **结果返回**: 生成交易应答报文

## 关键设计改进

相比原系统，重构后的系统在以下方面有重要改进:

1. **Saga模式**: 明确的补偿追踪和处理
2. **策略模式**: 可扩展的记账场景处理
3. **断路器**: 外部系统调用的弹性保护
4. **监控指标**: 完整的可观测性支持
5. **唯一流水号**: 保证幂等性
6. **领域模型**: 清晰的业务规则封装

## 配置说明

### 数据库配置

在 `application.yml` 中配置Oracle数据库连接:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:ORCL
    username: fund_user
    password: fund_password
```

### 断路器配置

```yaml
resilience4j:
  circuitbreaker:
    instances:
      coreBank:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 60000
```

## 数据库表结构

### 核心表

1. **SUBSCRIPTION_TRANSACTION** - 申购交易流水表
2. **SHARE_RECORD** - 份额记录表
3. **FUND_PRODUCT** - 基金产品表
4. **CUSTOMER_ACCOUNT** - 客户账户表
5. **COUPON_USAGE_RECORD** - 优惠券使用记录表

## API接口

### 基金申购

```http
POST /fund-sale/api/v1/subscriptions
Content-Type: application/json

{
  "customerId": "C123456",
  "accountNumber": "ACC789",
  "productCode": "FP001",
  "amount": 10000.00,
  "currencyCode": "CNY",
  "couponId": "CPN001",
  "channel": "WEB"
}
```

响应:
```json
{
  "success": true,
  "transactionSerialNumber": "SUB20231112120000000001",
  "customerId": "C123456",
  "productCode": "FP001",
  "subscriptionAmount": 10000.00,
  "finalFee": 50.00,
  "status": "SUCCESS"
}
```

## 构建和运行

### 构建项目

```bash
mvn clean install
```

### 运行应用

```bash
cd fund-application
mvn spring-boot:run
```

或使用指定的profile:

```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### 访问监控端点

- Health Check: http://localhost:8080/fund-sale/actuator/health
- Metrics: http://localhost:8080/fund-sale/actuator/metrics
- Prometheus: http://localhost:8080/fund-sale/actuator/prometheus

## 外部系统集成

本系统通过防腐层(ACL)与以下外部系统集成:

1. **核心银行系统** (`CoreBankingService`):
   - 记账操作
   - 冻结/解冻
   - 外币兑换+记账
   - 冲正

2. **营销系统** (`MarketingCouponService`):
   - 优惠券试算
   - 用券
   - 还券

注: 当前仅定义接口，需要根据实际的外部系统提供具体实现。

## 日志和监控

### 日志级别

- 开发环境: DEBUG
- 生产环境: INFO/WARN

### 监控指标

- `subscription.request`: 申购请求计数(按结果分类)
- `subscription.duration`: 申购处理耗时
- Circuit Breaker状态监控
- Database连接池监控

## 待实现功能

本次重构聚焦于**基金申购**业务，以下功能待后续实现:

- [ ] 基金赎回
- [ ] 基金分红
- [ ] TA文件处理
- [ ] 份额对账
- [ ] 报表生成

## 开发指南

### 添加新的限界上下文

1. 在根POM中添加新模块
2. 创建模块目录和POM
3. 定义领域模型(聚合根、实体、值对象)
4. 实现领域服务和仓储
5. 实现基础设施层(MyBatis mapper)

### 添加新的外部系统集成

1. 在 `fund-common/integration` 定义接口和DTO
2. 实现防腐层适配器
3. 配置断路器
4. 添加监控指标

## 贡献者

- 开发团队: 基金代销系统重构项目组

## 许可证

内部项目 - 保留所有权利

