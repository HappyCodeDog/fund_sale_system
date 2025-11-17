# DDD开发规范

## 1. 总体原则

### 1.1 核心原则

1. **限界上下文独立性**: 每个限界上下文应该独立演化，不直接依赖其他限界上下文的领域模型
2. **分层架构清晰**: 严格遵循分层架构，明确各层职责
3. **领域模型优先**: 业务逻辑封装在领域模型中，基础设施层只负责技术实现
4. **依赖方向**: 依赖方向始终指向领域层，领域层不依赖基础设施层

### 1.2 架构层次

```
┌─────────────────────────────────────┐
│      Application Layer              │  应用层：编排业务流程
├─────────────────────────────────────┤
│      Domain Layer                   │  领域层：业务逻辑核心
│  - Aggregate Root                  │
│  - Entity                          │
│  - Value Object                    │
│  - Domain Service                  │
│  - Repository Interface            │
├─────────────────────────────────────┤
│      Infrastructure Layer           │  基础设施层：技术实现
│  - Repository Implementation       │
│  - External System Adapter         │
│  - Database Mapper                 │
└─────────────────────────────────────┘
```

---

## 2. 限界上下文规范

### 2.1 限界上下文划分

每个限界上下文对应一个业务领域，具有独立的领域模型：

- **fund-trading**: 交易上下文（核心上下文）
- **fund-product**: 产品上下文
- **fund-customer**: 客户上下文
- **fund-marketing**: 营销上下文

### 2.2 限界上下文边界规则

✅ **允许**:
- 限界上下文可以依赖 `fund-common` 中的共享内核（如 `Money`、`ValueObject` 等）
- 限界上下文可以依赖 `fund-common` 中的防腐层接口（如 `CoreBankingService`、`MarketingCouponService`）

❌ **禁止**:
- 限界上下文**不能**直接依赖其他限界上下文的领域模型
- 限界上下文的领域层**不能**依赖其他限界上下文的领域服务
- 限界上下文的领域层**不能**依赖其他限界上下文的仓储

### 2.3 跨限界上下文交互

**规则**: 跨限界上下文的交互必须通过**应用层**进行协调，领域层不参与跨上下文交互。

**正确示例**:
```java
// ✅ 应用层协调跨上下文交互
@Service
public class SubscriptionApplicationService {
    private final ProductValidationService productValidationService;  // product上下文
    private final CustomerValidationService customerValidationService; // customer上下文
    private final SubscriptionValidationService validationService;     // trading上下文
    
    public SubscriptionResponse processSubscription(SubscriptionRequest request) {
        // 1. 产品验证（跨上下文）
        FundProduct product = productValidationService.validateForSubscription(...);
        
        // 2. 客户验证（跨上下文）
        CustomerAccount customer = customerValidationService.validateCustomerAccount(...);
        
        // 3. 交易上下文内的验证
        validationService.validateDailyQuota(product, amount);
    }
}
```

**错误示例**:
```java
// ❌ 领域服务直接依赖其他限界上下文的领域服务
@Service
public class SubscriptionValidationService {
    private final ProductValidationService productValidationService;  // ❌ 违反边界
    private final CustomerValidationService customerValidationService; // ❌ 违反边界
}
```

---

## 3. 分层架构规范

### 3.1 应用层 (Application Layer)

**职责**:
- 编排业务流程，协调多个领域服务
- 处理跨限界上下文的交互
- 管理事务边界
- 处理DTO转换
- 调用外部系统（通过防腐层接口）

**命名规范**:
- 类名: `{业务}ApplicationService`，如 `SubscriptionApplicationService`
- 包路径: `com.bank.fund.{context}.application`

**依赖规则**:
- ✅ 可以依赖领域层的领域服务、仓储接口
- ✅ 可以依赖其他限界上下文的领域服务（用于协调）
- ✅ 可以依赖防腐层接口（`fund-common.integration`）
- ❌ 不能依赖基础设施层的实现类

**示例**:
```java
package com.bank.fund.trading.application;

@Service
@RequiredArgsConstructor
public class SubscriptionApplicationService {
    // ✅ 依赖领域服务
    private final SubscriptionValidationService validationService;
    
    // ✅ 依赖其他限界上下文的领域服务（用于协调）
    private final ProductValidationService productValidationService;
    
    // ✅ 依赖防腐层接口
    private final MarketingCouponService marketingCouponService;
    
    // ✅ 依赖仓储接口
    private final SubscriptionTransactionRepository transactionRepository;
    
    @Transactional
    public SubscriptionResponse processSubscription(SubscriptionRequest request) {
        // 业务流程编排
    }
}
```

### 3.2 领域层 (Domain Layer)

**职责**:
- 封装业务逻辑和业务规则
- 定义聚合根、实体、值对象
- 定义领域服务（处理单一限界上下文内的复杂业务逻辑）
- 定义仓储接口

**包结构**:
```
domain/
├── model/          # 聚合根、实体、值对象
├── service/        # 领域服务
└── repository/     # 仓储接口
```

**依赖规则**:
- ✅ 可以依赖 `fund-common` 中的共享内核
- ✅ 可以依赖本限界上下文内的其他领域对象
- ❌ **不能**依赖其他限界上下文的领域模型
- ❌ **不能**依赖其他限界上下文的领域服务
- ❌ **不能**依赖基础设施层
- ❌ **不能**依赖应用层

### 3.3 基础设施层 (Infrastructure Layer)

**职责**:
- 实现仓储接口
- 实现防腐层接口（外部系统适配器）
- 数据库映射（MyBatis Mapper）
- 技术框架集成

**包结构**:
```
infrastructure/
├── persistence/    # 持久化实现
│   ├── {Entity}RepositoryImpl.java
│   └── {Entity}Mapper.java
└── adapter/        # 外部系统适配器（可选）
```

**依赖规则**:
- ✅ 可以依赖领域层的接口和模型
- ✅ 可以依赖技术框架（Spring、MyBatis等）
- ❌ 不能依赖应用层

---

## 4. 领域模型规范

### 4.1 聚合根 (Aggregate Root)

**定义**: 聚合根是聚合的入口点，外部只能通过聚合根访问聚合内的对象。

**规范**:
1. 必须实现 `AggregateRoot<ID>` 接口
2. 负责维护聚合内的业务不变性
3. 负责管理聚合内实体的生命周期
4. 提供业务方法，封装业务逻辑

**命名规范**:
- 类名使用业务术语，如 `SubscriptionTransaction`、`FundProduct`

**示例**:
```java
package com.bank.fund.trading.domain.model;

import com.bank.fund.common.domain.AggregateRoot;
import lombok.Data;

/**
 * Subscription Transaction Aggregate Root
 * Represents a complete fund subscription transaction with its lifecycle
 */
@Data
public class SubscriptionTransaction implements AggregateRoot<String> {
    private String id;
    private TransactionStatus status;
    private SagaState sagaState;
    
    /**
     * 业务方法：初始化交易
     */
    public void initialize() {
        if (this.status != null) {
            throw new IllegalStateException("Transaction already initialized");
        }
        this.status = TransactionStatus.INIT;
        this.sagaState = SagaState.INIT;
    }
    
    /**
     * 业务方法：标记校验通过
     */
    public void markValidated() {
        if (this.status != TransactionStatus.INIT) {
            throw new IllegalStateException("Transaction must be in INIT status");
        }
        this.status = TransactionStatus.VALIDATED;
    }
    
    /**
     * 维护业务不变性：确保金额一致性
     */
    public void updateFee(Money originalFee, Money discountAmount) {
        if (originalFee.isLessThan(discountAmount)) {
            throw new IllegalArgumentException("Discount cannot exceed original fee");
        }
        this.originalFee = originalFee;
        this.discountAmount = discountAmount;
        this.finalFee = originalFee.subtract(discountAmount);
    }
}
```

### 4.2 实体 (Entity)

**定义**: 具有唯一标识的对象，通过ID区分不同的实例。

**规范**:
1. 必须实现 `Entity<ID>` 接口
2. 具有唯一标识（ID）
3. 可以有生命周期
4. 可以包含业务逻辑

**示例**:
```java
package com.bank.fund.marketing.domain.model;

import com.bank.fund.common.domain.Entity;
import lombok.Data;

/**
 * Coupon usage record entity
 */
@Data
public class CouponUsageRecord implements Entity<String> {
    private String id;
    private String transactionSerialNumber;
    private String customerId;
    private String couponId;
    private CouponUsageStatus status;
    private LocalDateTime usedAt;
    private LocalDateTime returnedAt;
}
```

### 4.3 值对象 (Value Object)

**定义**: 没有唯一标识的对象，通过属性值定义相等性，应该是不可变的。

**规范**:
1. 必须实现 `ValueObject` 接口
2. 使用 `@Value` 注解（Lombok）确保不可变性
3. 所有字段应该是 `final` 的
4. 可以包含业务逻辑方法

**示例**:
```java
package com.bank.fund.marketing.domain.model;

import com.bank.fund.common.domain.ValueObject;
import lombok.Value;
import java.math.BigDecimal;

/**
 * Coupon information value object
 */
@Value
public class CouponInfo implements ValueObject {
    String couponId;
    String couponType;
    BigDecimal discountRate;
    BigDecimal discountAmount;
    
    /**
     * 业务方法：计算折扣后的费用
     */
    public BigDecimal calculateDiscountedFee(BigDecimal originalFee) {
        if (discountRate != null) {
            return originalFee.multiply(BigDecimal.ONE.subtract(discountRate));
        } else if (discountAmount != null) {
            BigDecimal discounted = originalFee.subtract(discountAmount);
            return discounted.max(BigDecimal.ZERO);
        }
        return originalFee;
    }
}
```

### 4.4 领域服务 (Domain Service)

**定义**: 当业务逻辑不属于任何单个实体或值对象时，使用领域服务。

**使用场景**:
1. 业务逻辑涉及多个实体
2. 业务逻辑需要访问仓储
3. 业务逻辑是单一限界上下文内的复杂计算

**规范**:
1. 类名: `{业务}Service`，如 `SubscriptionValidationService`
2. 包路径: `com.bank.fund.{context}.domain.service`
3. **只能处理本限界上下文内的业务逻辑**
4. **不能依赖其他限界上下文的领域服务**

**正确示例**:
```java
package com.bank.fund.trading.domain.service;

import com.bank.fund.trading.domain.repository.SubscriptionTransactionRepository;
import org.springframework.stereotype.Service;

/**
 * 领域服务：处理交易上下文内的验证逻辑
 */
@Service
@RequiredArgsConstructor
public class SubscriptionValidationService {
    
    // ✅ 只依赖本限界上下文的仓储
    private final SubscriptionTransactionRepository transactionRepository;
    
    /**
     * 验证日累计额度（本上下文内的业务逻辑）
     */
    public void validateDailyQuota(FundProduct product, Money amount) {
        // 使用本上下文的仓储查询
        BigDecimal currentUsage = getDailyUsage(product.getId());
        // 验证逻辑...
    }
    
    /**
     * 检查是否存在历史申购（本上下文内的业务逻辑）
     */
    public boolean hasExistingSubscription(String customerId, String productCode) {
        return transactionRepository.hasExistingSubscription(customerId, productCode);
    }
}
```

**错误示例**:
```java
// ❌ 领域服务依赖其他限界上下文的领域服务
@Service
public class SubscriptionValidationService {
    private final ProductValidationService productValidationService;  // ❌ 违反边界
    private final CustomerValidationService customerValidationService; // ❌ 违反边界
}
```

### 4.5 仓储接口 (Repository Interface)

**定义**: 定义聚合的持久化接口，属于领域层。

**规范**:
1. 接口定义在领域层: `com.bank.fund.{context}.domain.repository`
2. 接口名: `{Entity}Repository`，如 `SubscriptionTransactionRepository`
3. 方法使用领域术语，不暴露技术细节
4. 实现类在基础设施层

**示例**:
```java
package com.bank.fund.trading.domain.repository;

import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import java.util.Optional;

/**
 * Repository interface for subscription transactions
 */
public interface SubscriptionTransactionRepository {
    Optional<SubscriptionTransaction> findById(String id);
    void save(SubscriptionTransaction transaction);
    void update(SubscriptionTransaction transaction);
    boolean hasExistingSubscription(String customerId, String productCode);
}
```

**实现类**:
```java
package com.bank.fund.trading.infrastructure.persistence;

import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.repository.SubscriptionTransactionRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository implementation using MyBatis
 */
@Repository
@RequiredArgsConstructor
public class SubscriptionTransactionRepositoryImpl 
        implements SubscriptionTransactionRepository {
    
    private final SubscriptionTransactionMapper mapper;
    
    @Override
    public Optional<SubscriptionTransaction> findById(String id) {
        SubscriptionTransactionPO po = mapper.findById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }
    
    // ... 其他实现
}
```

---

## 5. 防腐层规范 (Anti-Corruption Layer)

### 5.1 防腐层定义

防腐层用于隔离外部系统，防止外部系统的变化污染领域模型。

### 5.2 防腐层接口位置

所有防腐层接口定义在 `fund-common.integration` 包中：

```
fund-common/
└── src/main/java/com/bank/fund/common/integration/
    ├── CoreBankingService.java          # 核心银行系统接口
    ├── MarketingCouponService.java      # 营销系统接口
    └── dto/                              # 数据传输对象
```

### 5.3 使用规范

1. **领域层和应用层**通过防腐层接口与外部系统交互
2. **基础设施层**实现防腐层接口（适配器模式）
3. 防腐层接口使用领域术语，不暴露外部系统的技术细节

**示例**:
```java
// ✅ 防腐层接口（fund-common）
package com.bank.fund.common.integration;

public interface CoreBankingService {
    AccountingResponse accounting(AccountingRequest request);
    FreezeResponse freeze(FreezeRequest request);
}

// ✅ 领域服务使用防腐层接口
@Service
public class AccountingService {
    private final CoreBankingService coreBankingService;  // ✅ 依赖接口
    // ...
}

// ✅ 基础设施层实现接口
@Component
public class CoreBankingServiceAdapter implements CoreBankingService {
    // 调用外部系统的HTTP/RPC接口
}
```

---

## 6. 命名规范

### 6.1 包命名

```
com.bank.fund.{context}/
├── application/              # 应用层
│   └── dto/                 # 数据传输对象
├── domain/                   # 领域层
│   ├── model/               # 聚合根、实体、值对象
│   ├── service/             # 领域服务
│   └── repository/          # 仓储接口
└── infrastructure/          # 基础设施层
    ├── persistence/         # 持久化实现
    └── adapter/             # 外部系统适配器（可选）
```

### 6.2 类命名

| 类型 | 命名规范 | 示例 |
|------|---------|------|
| 聚合根 | 业务术语 | `SubscriptionTransaction` |
| 实体 | 业务术语 | `CouponUsageRecord` |
| 值对象 | 业务术语 | `CouponInfo`、`Money` |
| 领域服务 | `{业务}Service` | `SubscriptionValidationService` |
| 应用服务 | `{业务}ApplicationService` | `SubscriptionApplicationService` |
| 仓储接口 | `{Entity}Repository` | `SubscriptionTransactionRepository` |
| 仓储实现 | `{Entity}RepositoryImpl` | `SubscriptionTransactionRepositoryImpl` |
| Mapper | `{Entity}Mapper` | `SubscriptionTransactionMapper` |
| DTO | `{业务}Request/Response` | `SubscriptionRequest` |

### 6.3 方法命名

- **业务方法**: 使用业务术语，如 `initialize()`、`markValidated()`、`calculateFee()`
- **查询方法**: 使用 `find`、`get`、`has` 前缀，如 `findById()`、`hasExistingSubscription()`
- **命令方法**: 使用动词，如 `save()`、`update()`、`delete()`

---

## 7. 依赖规则总结

### 7.1 依赖方向图

```
Application Layer
    ↓ (依赖)
Domain Layer (接口)
    ↑ (实现)
Infrastructure Layer
```

### 7.2 详细规则

| 层次 | 可以依赖 | 不能依赖 |
|------|---------|---------|
| **应用层** | 领域层（服务、仓储接口）<br>其他限界上下文的领域服务<br>防腐层接口<br>DTO | 基础设施层实现类 |
| **领域层** | `fund-common` 共享内核<br>本限界上下文内的领域对象<br>本限界上下文的仓储接口 | 其他限界上下文的领域模型<br>其他限界上下文的领域服务<br>基础设施层<br>应用层 |
| **基础设施层** | 领域层（接口和模型）<br>技术框架 | 应用层 |

### 7.3 跨限界上下文交互规则

**规则**: 跨限界上下文的交互**必须**在应用层进行协调。

**正确流程**:
```
Application Service (fund-trading)
    ├─→ ProductValidationService (fund-product)  ✅ 应用层协调
    ├─→ CustomerValidationService (fund-customer) ✅ 应用层协调
    └─→ SubscriptionValidationService (fund-trading) ✅ 本上下文
```

**错误流程**:
```
SubscriptionValidationService (fund-trading.domain)
    ├─→ ProductValidationService (fund-product.domain)  ❌ 领域层跨上下文
    └─→ CustomerValidationService (fund-customer.domain) ❌ 领域层跨上下文
```

---

## 8. 代码检查清单

### 8.1 开发前检查

- [ ] 确定业务逻辑属于哪个限界上下文
- [ ] 确定应该放在哪个层次（应用层/领域层/基础设施层）
- [ ] 检查是否违反依赖规则

### 8.2 领域模型检查

- [ ] 聚合根是否实现了 `AggregateRoot<ID>` 接口
- [ ] 实体是否实现了 `Entity<ID>` 接口
- [ ] 值对象是否实现了 `ValueObject` 接口，是否不可变
- [ ] 业务逻辑是否封装在领域模型中

### 8.3 领域服务检查

- [ ] 领域服务是否只处理本限界上下文内的业务逻辑
- [ ] 领域服务是否依赖了其他限界上下文的领域服务（❌ 禁止）
- [ ] 领域服务是否只依赖本限界上下文的仓储接口

### 8.4 应用服务检查

- [ ] 应用服务是否负责跨限界上下文的协调
- [ ] 应用服务是否依赖了基础设施层的实现类（❌ 禁止）
- [ ] 事务管理是否在应用层

### 8.5 仓储检查

- [ ] 仓储接口是否定义在领域层
- [ ] 仓储实现是否在基础设施层
- [ ] 仓储方法是否使用领域术语

---

## 9. 常见问题与解决方案

### 9.1 问题：领域服务需要调用其他限界上下文的服务

**错误做法**:
```java
// ❌ 领域服务直接依赖其他限界上下文
@Service
public class SubscriptionValidationService {
    private final ProductValidationService productValidationService;
}
```

**正确做法**:
```java
// ✅ 应用层协调跨上下文交互
@Service
public class SubscriptionApplicationService {
    private final ProductValidationService productValidationService;
    private final SubscriptionValidationService validationService;
    
    public void validate(SubscriptionRequest request) {
        // 应用层协调
        FundProduct product = productValidationService.validate(...);
        validationService.validateDailyQuota(product, amount);
    }
}
```

### 9.2 问题：需要访问其他限界上下文的数据

**解决方案**: 
1. 通过应用层调用其他限界上下文的领域服务
2. 如果数据需要共享，考虑使用共享内核（`fund-common`）
3. 如果数据需要同步，考虑使用领域事件（未来扩展）

### 9.3 问题：业务逻辑应该放在聚合根还是领域服务？

**判断标准**:
- **聚合根**: 逻辑只涉及单个聚合内的对象
- **领域服务**: 逻辑涉及多个实体，或需要访问仓储

**示例**:
```java
// ✅ 聚合根：只涉及自身状态
public class SubscriptionTransaction {
    public void markValidated() {
        this.status = TransactionStatus.VALIDATED;
    }
}

// ✅ 领域服务：需要访问仓储
@Service
public class SubscriptionValidationService {
    public void validateDailyQuota(...) {
        BigDecimal usage = repository.getDailyUsage(...);  // 需要仓储
    }
}
```

---

## 10. 最佳实践

### 10.1 聚合设计

1. **保持聚合小**: 聚合应该尽可能小，只包含必须一起维护业务不变性的对象
2. **通过ID引用**: 聚合之间通过ID引用，不直接持有对象引用
3. **业务不变性**: 聚合根负责维护聚合内的业务不变性

### 10.2 领域服务使用

1. **优先使用聚合根**: 如果业务逻辑可以放在聚合根中，优先使用聚合根
2. **无状态**: 领域服务应该是无状态的
3. **单一职责**: 每个领域服务只处理一个业务场景

### 10.3 应用服务设计

1. **薄应用层**: 应用层应该尽可能薄，只负责编排
2. **事务边界**: 事务边界应该在应用层
3. **异常处理**: 应用层负责异常处理和转换

### 10.4 测试策略

1. **领域层测试**: 单元测试，不依赖基础设施
2. **应用层测试**: 集成测试，可以mock其他限界上下文的服务
3. **基础设施层测试**: 集成测试，测试与数据库、外部系统的交互

---

## 11. 参考资料

- 《领域驱动设计》- Eric Evans
- 《实现领域驱动设计》- Vaughn Vernon
- 项目架构文档: `docs/架构设计说明.md`

---

**版本**: 1.0  
**最后更新**: 2024年  
**维护者**: 架构组


